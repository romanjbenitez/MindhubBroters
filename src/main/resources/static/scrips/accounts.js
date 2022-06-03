Vue.createApp({
  data() {
    return {
      client: [],
      accounts: [],
      transactions: [],
      balance: 0,
      firstName: "",
      lastName: "",
      email: "",
      day: 0,
      month: "",
      year: 0,
      hide: "hide",
      urlAccount: "http://localhost:8080/web/account.html?id=",
      loans: [],
    };
  },

  created() {
    axios
      .get("http://localhost:8080/api/clients/current")
      .then((api) => {
        this.client = api.data;
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.transactions =
          this.accounts.length > 1
            ? api.data.accounts[0].transactions.concat(
                this.accounts[1].transactions
              )
            : null;
        this.firstName = api.data.firstName;
        this.lastName = api.data.lastName;
        this.email = api.data.email;
        this.loans = api.data.loans;
      })
      .catch((err) => console.log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    formatDate(date) {
      let newDate = date.split("T")[0];
      let time = date.split("T")[1].split(".")[0];
      return newDate + " at " + time;
    },

    addAccount() {
      Swal.fire({
        title: 'Do you want add a new account?',
        showDenyButton: true,
        confirmButtonText: 'Add new Account',
        denyButtonText: `Don't add`,
      }).then((result) => {
        
        if (result.isConfirmed) {
          axios
          .post("/api/clients/current/accounts", {
            headers: { "content-type": "application/x-www-form-urlencoded" },
          })
          .then((response) => {
            axios
              .get("http://localhost:8080/api/clients/current")
              .then(
                (api) =>
                  (this.accounts = api.data.accounts.sort((a, b) => a.id - b.id))
              );
              Swal.fire("Account created successfully", '', 'success')
          })
          .catch((err) => Swal.fire("You don't could have more than three account", '', 'info')
          );
        } else if (result.isDenied) {
          Swal.fire('Action canceled', '', 'success')
        }
      })
     
    },
    logout() {
      axios
        .post("/api/logout")
        .then((response) => window.location.replace("./index.html"));
    },
    doTransactions(){
      window.location.href = "/web/transfers.html"
    }
  },

  computed: {
    getToday() {
      let date = new Date();
      this.day = date.getDate();
      this.year = date.getFullYear();
      this.month = date.toDateString().split(" ")[1];
    },
  },
}).mount("#app");
