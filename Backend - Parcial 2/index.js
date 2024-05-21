const express = require('express');
const mongoose = require('mongoose');
const multer = require('multer');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;
const user = 'user-p';
const password = 'z8QxcRwfofgf9vgq';
const database = 'Veterinaria';
const uri = `mongodb+srv://${user}:${password}@cluster0.qaqql7a.mongodb.net/${database}?retryWrites=true&w=majority&appName=Cluster0`;

// MongoDB setup
mongoose.connect(uri, {
  useNewUrlParser: true,
  useUnifiedTopology: true
}).then(() => {
  console.log('Connected to MongoDB');
}).catch(err => {
  console.error('MongoDB connection error:', err);
});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));
db.once('open', () => console.log('Connected to MongoDB'));

app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Pet schema
const petSchema = new mongoose.Schema({
  type: { type: String, enum: ['cat', 'dog'], required: true },
  name: { type: String, required: true },
  age: { type: Number, required: true },
  breed: { type: String, required: true },
  image: { type: String, required: true }
});
const Pet = mongoose.model('Pet', petSchema);

// Multer setup for file upload
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/')
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname))
  }
});

const upload = multer({ storage: storage });

// Middleware to parse JSON
app.use(express.json());

// Service 1: Store a pet
app.post('/pets', upload.single('image'), async (req, res) => {
  try {
    const { type, name, age, breed } = req.body;
    const image = req.file.filename;

    const newPet = new Pet({ type, name, age, breed, image });
    await newPet.save();
    res.status(201).json({ message: 'Pet stored successfully' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Service 2: Get all pets
app.get('/pets', async (req, res) => {
  try {
    const petsDB = await Pet.find();

    const pets = petsDB.map(pet => ({
      ...pet._doc,
      image: `http://${req.headers.host}/uploads/${pet.image}`
    }));

    res.json(pets);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Service 3: Filter pets by name and sort by breed
app.get('/pets/filter', async (req, res) => {
  try {
    const { name, sortBy } = req.query;
    const query = name ? { name: { $regex: name, $options: 'i' } } : {};
    const petsDB = await Pet.find(query).sort(sortBy || 'breed');

    const pets = petsDB.map(pet => ({
      ...pet._doc,
      image: `http://${req.headers.host}/uploads/${pet.image}`
    }));

    res.json(pets);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Start server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
