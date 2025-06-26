// build/link-src.js
const fs = require('fs');
const path = require('path');

const linkPath = path.join(__dirname,'hwcsrc');
const targetPath = path.resolve(__dirname, '..','..', 'src');

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

var linkPathLogin = path.join(__dirname, 'login_module_domain');
var targetPathLogin = path.resolve(__dirname, "../../../../../modules/login/frontendTS/build/frontendGen/build/appdomain");

try {
  if (!fs.existsSync(linkPathLogin)) {
    fs.symlinkSync(targetPathLogin, linkPathLogin, 'junction');
    console.log(`✅ Symlink created: ${linkPathLogin} → ${targetPathLogin}`);
  } else {
    console.log('ℹ️ Symlink already exists:', linkPathLogin);
  }
} catch (err) {
  console.error('❌ Failed to create symlink:', err.message);
  process.exit(1);
}

linkPathLogin = path.join(__dirname, 'login_module_gui');
targetPathLogin = path.resolve(__dirname, "../../../../../modules/login/frontendTS/build/frontendGen/build/appgui");

try {
  if (!fs.existsSync(linkPathLogin)) {
    fs.symlinkSync(targetPathLogin, linkPathLogin, 'junction');
    console.log(`✅ Symlink created: ${linkPathLogin} → ${targetPathLogin}`);
  } else {
    console.log('ℹ️ Symlink already exists:', linkPathLogin);
  }
} catch (err) {
  console.error('❌ Failed to create symlink:', err.message);
  process.exit(1);
}

linkPathLogin = path.join(__dirname, "umlp_modules/loginmodule-ts/template_placeholder/src");
targetPathLogin = path.resolve(__dirname, "../../../../../modules/login/frontendTS/src/app");

try {
  if (!fs.existsSync(linkPathLogin)) {
    fs.symlinkSync(targetPathLogin, linkPathLogin, 'junction');
    console.log(`✅ Symlink created: ${linkPathLogin} → ${targetPathLogin}`);
  } else {
    console.log('ℹ️ Symlink already exists:', linkPathLogin);
  }
} catch (err) {
  console.error('❌ Failed to create symlink:', err.message);
  process.exit(1);
}

linkPathLogin = path.join(__dirname, "build/umlp_frontend");
targetPathLogin = path.resolve(__dirname, "../umlp_frontend");

try {
  if (!fs.existsSync(linkPathLogin)) {
    fs.symlinkSync(targetPathLogin, linkPathLogin, 'junction');
    console.log(`✅ Symlink created: ${linkPathLogin} → ${targetPathLogin}`);
  } else {
    console.log('ℹ️ Symlink already exists:', linkPathLogin);
  }
} catch (err) {
  console.error('❌ Failed to create symlink:', err.message);
  process.exit(1);
}

