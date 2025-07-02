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

var linkPath = path.join(__dirname,'hwcsrc');
var targetPath = path.resolve(__dirname, '..','..', 'src/main/ts');

linking(linkPath, targetPath)

linkPath = path.join(__dirname, "build/umlp_frontend");
targetPath = path.resolve(__dirname, "../umlp_frontend");

linking(linkPath, targetPath)

const umlpPath = path.join(__dirname,'node_modules/@umlp')
if (!fs.existsSync(umlpPath)){
  fs.mkdirSync(umlpPath);
}
