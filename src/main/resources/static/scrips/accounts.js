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
      urlAccount: "https://mhb-online-banking.herokuapp.com/web/account.html?id=",
      loans: [],
      income: 0,
      expense: 0,
      installments: 0,
      loanToPay: 0,
      transactionsSort: [],
      userProfille: null,
      charging: true,
      clientRole: ""
    };
  },

  created() {
    axios
      .get("/api/clients/current")
      .then((api) => {
        this.client = api.data;
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.transactions = this.accounts.length === 1 ? api.data.accounts[0].transactions : null
        this.transactions = this.accounts.length > 1 && this.accounts.length != null ? api.data.accounts[0].transactions.concat(this.accounts[1].transactions).sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()) : this.transactions;
        this.transactions = this.accounts[2] ? this.transactions.concat(this.accounts[2].transactions).sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()) : this.transactions;


        this.firstName = api.data.firstName;
        this.clientRole = api.data.clientRole
        this.userProfille = api.data.imgProfile == null ? null : "../assets/usersProfiles/" + api.data.imgProfile;
        this.lastName = api.data.lastName;
        this.email = api.data.email;
        this.loans = api.data.loans;

        this.income = this.transactions == null ? 0 : this.transactions.filter((transaction) => transaction.type == "CREDIT").reduce((acc, item) => { return acc + item.amount; }, 0);
        this.expense = this.transactions == null ? 0 : this.transactions.filter((transaction) => transaction.type == "DEBIT").reduce((acc, item) => { return acc + item.amount; }, 0);
        this.loanToPay = this.loans.reduce((acc, item) => {
          return acc + item.amount;
        }, 0);
        this.installments = this.loans.reduce((acc, item) => {
          return Math.round(acc + item.amount / item.payments);
        }, 0);
        setTimeout(() => { this.charging = false }, 1000)

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
                  .get("/api/clients/current")
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
    doTransactions() {
      window.location.href = "/web/transfers.html";
    },
    applyLoans() {
      window.location.href = "/web/loan-application.html";
    },
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
