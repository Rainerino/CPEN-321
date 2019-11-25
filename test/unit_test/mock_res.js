class Reponse {
  async send(message) {
    this.message = message;
  }

  async status(code) {
    this.code = code;
  }

  async json(data) {
    this.data = data;
  }
}
module.exports = Reponse;
