Vue.createApp({
  data() {
    return {
      newClient: false,
      loginPassword: "",
      loginEmail: "",
      registerEmail: "",
      registerFirstName: "",
      registerLastName: "",
      registerPassword: "",
      emailRegex: /^[-\w.%+]{1,64}@(?:[A-Z0-9-]{1,63}\.){1,125}[A-Z]{2,63}$/i,
      passwordRegex: /^(?=.[a-z])(?=.[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/i,
      nameRegex: /^[A-Z]+$/i,
    };
  },

  created() {},

  methods: {
    signIn() {
      if (this.emailRegex.test(this.loginEmail)) {
        axios
          .post(
            "/api/login",
            `email=${this.loginEmail}&password=${this.loginPassword}`,
            { headers: { "content-type": "application/x-www-form-urlencoded" } }
          )
          .then((response) => {
            Swal.fire({
              title: "Success!",
              text: "Login Successfully",
              icon: "success",
              confirmButtonText: "Close",
            }).then(result => {
              if (result.isConfirmed) {
                setTimeout(() => {
                  window.location.replace("./accounts.html");
                }, 500);
              }

            });;
          })
          .catch((err) =>
            Swal.fire({
              title: "Error!",
              text: "Password or email wrong",
              icon: "error",
              confirmButtonText: "Close",
            })
          );
      } else {
        Swal.fire({
          title: "Error!",
          text: "Please enter a valid email",
          icon: "error",
          confirmButtonText: "Close",
        });
      }
    },
    handleClient() {
      this.newClient = !this.newClient ? true : false;
    },
    register() {
      if (
        !this.nameRegex.test(this.registerLastName) ||
        !this.nameRegex.test(this.registerFirstName)
      ) {
        Swal.fire({
          title: "Warning!",
          text: `please enter a valid${
            this.nameRegex.test(this.registerFirstName)
              ? " lastname"
              : " firstname"
          }`,
          icon: "warning",
          confirmButtonText: "Close",
        });
      } else if (!this.emailRegex.test(this.registerEmail)) {
        Swal.fire({
          title: "Warning!",
          text: "Please enter a valid email",
          icon: "warning",
          confirmButtonText: "Close",
        });
      } else if (!this.passwordRegex.test(this.registerPassword)) {
        Swal.fire({
          title: "Warning!",
          text: "The password must have at least 8 characters, at least one uppercase letter, one lowercase letter and one number:",
          icon: "warning",
          confirmButtonText: "Close",
        });
      } else {
        axios
          .post(
            "/api/clients",
            `firstName=${this.registerFirstName}&lastName=${this.registerLastName}&email=${this.registerEmail}&password=${this.registerPassword}`,
            { headers: { "content-type": "application/x-www-form-urlencoded" } }
          )
          .then((response) => {
            axios
              .post(
                "/api/login",
                `email=${this.registerEmail}&password=${this.registerPassword}`,
                {
                  headers: {
                    "content-type": "application/x-www-form-urlencoded",
                  },
                }
              )
              .then((response) => {
                axios.post("/api/clients/current/accounts", {
                  headers: {
                    "content-type": "application/x-www-form-urlencoded",
                  },
                });
                Swal.fire({
                  title: "Success!",
                  text: "Resgister Successfully",
                  icon: "success",
                  confirmButtonText: "Close",
                  
                }).then(result => {
                  if (result.isConfirmed) {
                    setTimeout(() => {
                      window.location.replace("./accounts.html");
                    }, 500);
                  }

                });
                
              })
              .catch((err) => console.log(err));
          })
          .catch((err) => console.log(err));
      }
    },
  },

  computed: {},
}).mount("#app");



