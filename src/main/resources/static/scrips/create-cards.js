Vue.createApp({
  data() {
    return {
      urlAccount: "http://localhost:8080/web/account.html?id=",
      accounts: [],
      hide: "hide",
      balance: 0,
      color: "GOLD",
      type: "DEBIT",
      cards: [],
      filteredCardsColor: [],
      filteredCard: "",
      filteredCardsNumber: [],
      cardColor: "",
    };
  },

  created() {
    axios
      .get("/api/clients/current")
      .then((api) => {
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.lastName = api.data.lastName;
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
        this.cards = api.data.cards;
      })
      .catch((err) => console.log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
    },
    deleteCard() {
      Swal.fire({
        title: "Are you sure you want to delete this card?",
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: "Yes, I'm sure",
        denyButtonText: `I'm not sure`,
      }).then((result) => {
        if (result.isConfirmed) {
          axios
            .post(
              "/api/clients/current/cards/delete",
              `cardNumber=${this.filteredCard}`,
              {
                headers: {
                  "content-type": "application/x-www-form-urlencoded",
                },
              }
            )
            .then((res) => {
              Swal.fire("Card deleted successfully", "", "success");
              setTimeout(() => {
                window.location.replace("./cards.html");
              }, 1000);
            })
            .catch((err) => Swal.fire(`${err}`, "", "info"));
        } else if (result.isDenied) {
          Swal.fire("Changes are not saved", "", "info");
        }
      });
    },

    createCard() {
      Swal.fire({
        title: "Are you sure you want to get a new card?",
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: "Yes, I'm sure",
        denyButtonText: `I'm not sure`,
      }).then((result) => {
        if (result.isConfirmed) {
          axios
            .post(
              "/api/clients/current/cards",
              `color=${this.color}&type=${this.type}`,
              {
                headers: {
                  "content-type": "application/x-www-form-urlencoded",
                },
              }
            )
            .then((res) => {
              Swal.fire("Card created successfully", "", "success");
              setTimeout(() => {
                window.location.replace("./cards.html");
              }, 1000);
            })
            .catch((err) =>
              Swal.fire("Something got wrong, please try later", "", "info")
            );
        } else if (result.isDenied) {
          Swal.fire("Changes are not saved", "", "info");
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
  },

  computed: {
    getToday() {
      let date = new Date();
      this.day = date.getDate();
      this.year = date.getFullYear();
      this.month = date.toDateString().split(" ")[1];
    },
    filterSelectCardsNumbers() {
      let cards = [...this.cards];
      if (cards.length > 0) {
        this.filteredCardsColor = cards.filter(
          (card) => card.type == this.type
        );
        this.cardColor = this.filteredCardsColor[0].color;
        this.filteredCardsNumber = this.filteredCardsColor.filter(
          (card) => card.color == this.cardColor
        );
        this.filteredCard = this.filteredCardsNumber[0].number;
      }
    },
  },
}).mount("#app");
