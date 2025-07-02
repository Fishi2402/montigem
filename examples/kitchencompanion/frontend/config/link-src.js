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

const linkPath = path.join(__dirname,'hwcsrc');
const targetPath = path.resolve(__dirname, '..','..', 'src/app');

linking(linkPath, targetPath)
