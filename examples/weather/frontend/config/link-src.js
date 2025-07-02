// build/link-src.js
const fs = require('fs');
const path = require('path');

function linking(linkPath, targetPath) {
  try {
    if (!fs.existsSync(linkPath)) {
      fs.symlinkSync(targetPath, linkPath, 'junction');
      console.log(`✅ Symlink created: ${linkPath} → ${targetPath}`);
    } else {
      console.log('ℹ️ Symlink already exists:', linkPath);
    }
  } catch (err) {
    console.error('❌ Failed to create symlink:', err.message);
    process.exit(1);
  }
}


const hwcsrc = path.join(__dirname,'hwcsrc');

if(!fs.existsSync(hwcsrc)) {
  fs.mkdirSync(path.join(__dirname,'hwcsrc'))
}

const linkPath = path.join(__dirname,'hwcsrc/ts');
const targetPath = path.resolve(__dirname, '..','..', 'src/app');

linking(linkPath, targetPath)

const linkPath1 = path.join(__dirname, "build/umlp_frontend");
const targetPath1 = path.resolve(__dirname, "../umlp_frontend");

linking(linkPath1, targetPath1)
