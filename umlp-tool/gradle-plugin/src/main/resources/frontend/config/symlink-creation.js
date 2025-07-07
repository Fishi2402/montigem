// build/link-src.js
const fs = require('fs');

function linking(linkPath, targetPath) {
  try {
    if (!fs.existsSync(linkPath)) {
      const parentDir = path.dirname(linkPath);
      fs.mkdirSync(parentDir, { recursive: true })
      
      fs.symlinkSync(targetPath, linkPath, 'junction');
      console.log(`✅ Symlink created: ${linkPath} → ${targetPath}`);
    } else {
      console.log('ℹ️ Symlink already exists:', linkPath);
    }
  } catch (err) {
    console.log('❌ Failed to create symlink:', err.message);
    console.error('❌ Failed to create symlink:', err.message);
    process.exit(1);
  }
}

const args = process.argv.slice(2)
if((args.length % 2) !== 0) {
  console.error('❌ Please give link and target SymLinks in pairs.');
} else {
  console.log("Using following args for symbolic linking: ", args)
}

for (let i = 0; i < args.length-1; i++) {
  linking(args[i], args[i+1])
  i++;
}
