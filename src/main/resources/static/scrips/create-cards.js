Vue.createApp({
  data() {
    return {
      accounts: [],
      hide: "hide",
      balance: 0,
      color: "GOLD",
      type: "DEBIT",
    };
  },

  created() {
    axios
      .get("http://localhost:8080/api/clients/current")
      .then((api) => {
        this.accounts = api.data.accounts.sort((a, b) => a.id - b.id);
        this.lastName = api.data.lastName;
        this.balance = this.accounts
          .filter((account) => account.balance != 0)
          .reduce((acc, item) => {
            return acc + item.balance;
          }, 0);
      })
      .catch((err) => console / log(err));
  },

  methods: {
    hideBalance() {
      this.hide == "hide" ? (this.hide = "show") : (this.hide = "hide");
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
              });
            }, 1000);
        } else if (result.isDenied) {
          Swal.fire("Changes are not saved", "", "info");
        }
      });
    },
    addAccount() {
      Swal.fire({
        title: "Do you want add a new account?",
        showDenyButton: true,
        confirmButtonText: "Add new Account",
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
                    (this.accounts = api.data.accounts.sort(
                      (a, b) => a.id - b.id
                    ))
                );
              Swal.fire("Account created successfully", "", "success");
            })
            .catch((err) =>
              Swal.fire(
                "You don't could have more than three account",
                "",
                "info"
              )
            );
        } else if (result.isDenied) {
          Swal.fire("Action canceled");
        }
      });
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
