module.exports = {
  context:["/umlp/api"],
  target: process.env.BACKEND_PROXY_ADDRESS || "http://localhost:8081/",
  secure: false,
  ws: true
}
