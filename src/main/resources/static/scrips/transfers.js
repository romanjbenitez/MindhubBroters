Vue.createApp({
  data() {
    return {
      accounts: [],
      hide: "hide",
      balance: 0,
      transactions: [],
      account: "Own",
      originAccount: "",
      selectedOriginAccount: [],
      destinationAccount: "",
      amount: 0,
      description: "",
      urlAccount: "http://localhost:8080/web/account.html?id=",
    };
  },

  created() {
    axios
      .get("http://localhost:8080/api/clients/current")
      .then((api) => {
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.originAccount = this.accounts[0].number;
      })
      .catch((err) => console / log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    createTransfers() {
      Swal.fire({
        title: `Are you sure to transfer $${this.amount} to the account number ${this.destinationAccount}`,
        showDenyButton: true,
        confirmButtonText: "Transfer!!",
        denyButtonText: `Don't do that`,
      }).then((result) => {
        if (result.isConfirmed) {
          axios
            .post(
              "/api/transactions",
              `ammount=${this.amount}&description=${this.description}&originNumber=${this.selectedOriginAccount[0].number}&destinationNumber=${this.destinationAccount}`,
              {
                headers: {
                  "content-type": "application/x-www-form-urlencoded",
                },
              }
            )
            .then((res) => {
              Swal.fire({
                title: "Success!",
                text: "Transfer Successfully",
                icon: "success",
                confirmButtonText: "Close",
              }).then((result) => {
                if (result.isConfirmed) {
                  setTimeout(() => {
                    window.location.replace("./accounts.html");
                  }, 500);
                }
                
              });
            })
            .catch((err) =>
              Swal.fire({
                title: "Error!",
                text: `${err.response.data}`,
                icon: "error",
                confirmButtonText: "Close",
              })
            );
        }
        else{
          Swal.fire("Action canceled");
        }
      });
    },
    async addAccount() {

      const { value: accountType } = await Swal.fire({
        title: 'Select an account type',
        input: 'select',
        inputOptions: {
          'Saving': 'Saving',
          'Checking': 'Checking'
        },
        inputPlaceholder: 'Select an account type',
        showCancelButton: true,
      });
      if (accountType) {
        Swal.fire({
          title: 'Do you want add a new account?',
          showDenyButton: true,
          confirmButtonText: 'Add new Account',
          denyButtonText: `Don't add`,
        }).then((result) => {
          if (result.isConfirmed) {
            axios
              .post("/api/clients/current/accounts", `accountType=${accountType}`, {
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
            Swal.fire('Action canceled')
          }
        })
      }
    },
    logout() {
      axios
        .post("/api/logout")
        .then((response) => window.location.replace("./index.html"));
    },
  },

  computed: {
    getToday() {
      let date = new Date();
      this.day = date.getDate();
      this.year = date.getFullYear();
      this.month = date.toDateString().split(" ")[1];
    },
    filterOriginAccount() {
      this.selectedOriginAccount = this.accounts.filter(
        (acc) => acc.number === this.originAccount
      );
    },
  },
}).mount("#app");
