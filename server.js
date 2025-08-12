const express = require('express');
const multer = require('multer');
const unzipper = require('unzipper');
const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');

const app = express();
const upload = multer({ dest: 'uploads/' });

app.post('/upload', upload.single('zipfile'), async (req, res) => {
  const zipPath = req.file.path;
  const extractDir = path.join(__dirname, 'builds', Date.now().toString());
  fs.mkdirSync(extractDir, { recursive: true });

  await fs.createReadStream(zipPath).pipe(unzipper.Extract({ path: extractDir })).promise();

  exec(`chmod +x gradlew`, { cwd: extractDir }, (err) => {
    if (err) return res.status(500).send('Failed to set gradlew executable');

    exec(`./gradlew assembleDebug`, { cwd: extractDir }, (err) => {
      if (err) return res.status(500).send('Build failed: ' + err.message);

      const apkPath = path.join(extractDir, 'app/build/outputs/apk/debug/app-debug.apk');
      if (!fs.existsSync(apkPath)) return res.status(500).send('APK not found after build.');

      res.download(apkPath, 'app-debug.apk');
    });
  });
});

app.use(express.static('public'));

app.listen(3000, () => console.log('Server running on port 3000'));
