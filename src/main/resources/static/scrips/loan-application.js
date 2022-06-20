Vue.createApp({
  data() {
    return {
      urlAccount: "http://localhost:8080/web/account.html?id=",
      accounts: [],
      hide: "hide",
      balance: 0,
      description: "",
      loans: [],
      clientLoans: [],
      loansForCurrentClient: [],
      selectLoan: [],
      availableLoans: "",
      selectedOriginAccount: [],
      amount: 0,
      selectPayments: [],
      payments: 0,
      originAccount: "",
      loanId: "",
      loanPercent: 0
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
        this.clientLoans = api.data.loans;
      })
      .catch((err) => console / log(err));
    axios.get("http://localhost:8080/api/loans").then((api) => {
      this.loans = api.data;
      this.loansForCurrentClient = this.loans.filter((loan) => {
        let response = this.clientLoans.find((clientLoan) => {
          return clientLoan.id === loan.id;
        });
        return response == undefined;
      });
      this.availableLoans = this.loansForCurrentClient[0].name;
    });
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    createLoan() {
      let loanID = this.loans.filter(loan => loan.name === this.availableLoans)[0].id
      Swal.fire({
        title: "Do you want apply a new loan?",
        showDenyButton: true,
        confirmButtonText: "Apply",
        denyButtonText: `Don't apply`,
      }).then((result) => {
        if (result.isConfirmed) {
          axios
            .post("/api/loans", {
              id: loanID,
              ammount: this.amount,
              payments: this.payments,
              accountNumber: this.originAccount,
            })
            .then((res) => {
              Swal.fire({
                title: "Success!",
                text: "Apply created successfully, your will be credited soon",
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
    filterAvailableLoan() {
      this.selectLoan = this.loansForCurrentClient.filter(
        (loan) => loan.name === this.availableLoans
      );
      if (this.selectLoan.length >= 1) {
        this.selectPayments = this.selectLoan[0].payments;
        this.payments = this.selectPayments[0];
      }
    },
    filterPercent(){
      if(this.loans.length > 1){
        this.loanPercent = this.loans.filter(loan => loan.name === this.availableLoans)[0].interestPercentage
      }
    }
    
  },
}).mount("#app");


