Vue.createApp({
  data() {
    return {
      jsonHtml: "",
      clients: [],
      firstName: "",
      lastName: "",
      email: "",
      arrayIds: [],
      idSelectedModify: "SelecId",
      clientFilteredModify: [],
      firstNameModify: "",
      lastNameModify: "",
      emailModify: "",
    };
  },

  created() {
    axios.get("http://localhost:8080/api/clients").then((api) => {
      this.clients = api.data
      this.arrayIds = this.clients.map(client => client.id)
    }).catch(err => console.log(err));
  },

  methods: {
    addClient() {
      client = {
        firstName: this.firstName,
        lastName: this.lastName,
        email: this.email,
      };
      axios
        .post("http://localhost:8080/rest/clients", client)
        .then((api) => {
          console.log(client)

          this.updateTable();
          console.log(api.data)
        })
        .catch((err) => console.log(err));
    },
    deleteClient(index) {
      axios
        .delete(`http://localhost:8080/rest/clients/${index}`)
        .then((api) => {
          this.updateTable();
        })
        .catch((err) => console.log(err));
    },
    modifyClient(){
      client = {
        firstName: this.firstNameModify,
        lastName: this.lastNameModify,
        email: this.emailModify,
      };
      axios.put(`http://localhost:8080/api/clients${this.idSelectedModify}`, client).then((api) => {
        this.updateTable();
      })
      .catch((err) => console.log(err));
    },
    updateTable() {
      axios.get("http://localhost:8080/api/clients").then((api) => {
        this.clients = api.data;
        this.arrayIds = this.clients.map(client => client.id)
      });
    },

   

  },

  computed: {
    filterClient() {
      if (this.clients.length > 1 && this.idSelectedModify != "SelecId") {
        this.clientFilteredModify = this.clients.filter((client) => {
          let idModify = client._links.client.href.substring(30);
          return idModify == this.idSelectedModify;
        });
        this.firstNameModify = this.clientFilteredModify[0].firstName;
        this.lastNameModify = this.clientFilteredModify[0].lastName;
        this.emailModify = this.clientFilteredModify[0].email;
      }
      else{
        this.firstNameModify = ""
        this.lastNameModify = ""
        this.emailModify = ""
      }
    },
  },
}).mount("#app");
