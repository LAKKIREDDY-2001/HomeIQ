const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 10000;

// Serve static assets from project root and docs
app.use(express.static(path.join(__dirname, '.')));
app.use('/docs', express.static(path.join(__dirname, 'docs')));

// Always serve index.html for main dashboard routes
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(PORT, () => {
  console.log(`HomeIQ Fire TV Web Simulator live on port ${PORT}`);
});
