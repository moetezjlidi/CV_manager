<template>
  <div>
    <div class="mb-4">
      <button @click="goBack" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Retour
      </button>
    </div>

    <div class="person-form">
      <div class="card shadow">
        <div class="card-header bg-primary text-white">
          <h3 class="mb-0">
            <i class="bi bi-person-plus"></i> Coopter une Personne
          </h3>
          <p class="mb-0 mt-2 small">
            Créer un CV pour quelqu'un qui n'a pas encore de compte. Cette personne pourra le réclamer plus tard avec son email.
          </p>
        </div>
        
        <div class="card-body">
          <form @submit.prevent="handleSubmit">
            <div class="row">
              
              <div class="col-md-6 mb-3">
                <label class="form-label">Prénom <span class="text-danger">*</span></label>
                <input 
                  v-model="form.firstName" 
                  type="text"
                  class="form-control" 
                  :class="{'is-invalid': errors.firstName}"
                  placeholder="Jean"
                  required
                />
                <div v-if="errors.firstName" class="invalid-feedback">
                  {{ errors.firstName }}
                </div>
              </div>
              
              <div class="col-md-6 mb-3">
                <label class="form-label">Nom <span class="text-danger">*</span></label>
                <input 
                  v-model="form.lastName" 
                  type="text"
                  class="form-control" 
                  :class="{'is-invalid': errors.lastName}"
                  placeholder="Dupont"
                  required
                />
                <div v-if="errors.lastName" class="invalid-feedback">
                  {{ errors.lastName }}
                </div>
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Email <span class="text-danger">*</span></label>
                <input 
                  v-model="form.email" 
                  type="email"
                  class="form-control" 
                  :class="{'is-invalid': errors.email}"
                  placeholder="jean.dupont@email.com"
                  required
                />
                <div v-if="errors.email" class="invalid-feedback">
                  {{ errors.email }}
                </div>
                <small class="form-text text-muted">
                  Cet email permettra à la personne de réclamer son CV plus tard
                </small>
              </div>
              
              <div class="col-md-6 mb-3">
                <label class="form-label">Date de naissance</label>
                <input 
                  v-model="form.birthDate" 
                  type="date"
                  class="form-control"
                />
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-12 mb-3">
                <label class="form-label">Site Web</label>
                <input 
                  v-model="form.website" 
                  type="url"
                  class="form-control"
                  placeholder="https://www.monsite.com"
                />
              </div>
            </div>
            
            <div class="alert alert-info">
              <i class="bi bi-info-circle"></i> 
              <strong>Information :</strong> Un email d'invitation sera envoyé à cette personne avec un lien pour créer son compte et réclamer son CV.
            </div>
            
            <div v-if="successMessage" class="alert alert-success">
              <i class="bi bi-check-circle"></i> {{ successMessage }}
            </div>
            <div v-if="generalError" class="alert alert-danger">
              <i class="bi bi-x-circle"></i> {{ generalError }}
            </div>
            
            <div class="d-flex justify-content-end gap-2 mt-4">
              <button type="button" @click="goBack" class="btn btn-secondary">
                <i class="bi bi-x-circle"></i> Annuler
              </button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">
                <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-envelope"></i> 
                Envoyer l'invitation
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  inject: ['user'],
  data() {
    return {
      form: {
        firstName: '',
        lastName: '',
        email: '',
        birthDate: '',
        website: ''
      },
      errors: {},
      generalError: null,
      successMessage: null,
      submitting: false
    };
  },
  methods: {
    validateForm() {
      this.errors = {};
      
      if (!this.form.firstName || this.form.firstName.trim().length < 2) {
        this.errors.firstName = 'Le prénom doit contenir au moins 2 caractères';
      }
      
      if (!this.form.lastName || this.form.lastName.trim().length < 2) {
        this.errors.lastName = 'Le nom doit contenir au moins 2 caractères';
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.form.email || !emailRegex.test(this.form.email)) {
        this.errors.email = 'Email invalide';
      }
      
      return Object.keys(this.errors).length === 0;
    },
    
    handleSubmit() {
      this.generalError = null;
      this.successMessage = null;
      
      if (!this.validateForm()) {
        return;
      }
      
      if (!this.user || !this.user.authenticated) {
        this.generalError = 'Vous devez être connecté pour créer un CV';
        return;
      }
      
      this.submitting = true;
      
      const invitationData = {
        firstName: this.form.firstName.trim(),
        lastName: this.form.lastName.trim(),
        email: this.form.email.trim().toLowerCase(),
        birthDate: this.form.birthDate || null,
        website: this.form.website || null
      };
      
      axios.post('/api/persons/send-invitation', invitationData, {
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('jwtToken')}`
        }
      })
      .then(response => {
        this.successMessage = `Invitation envoyée avec succès à ${invitationData.email} !`;
        this.submitting = false;
        
  
        this.form = {
          firstName: '',
          lastName: '',
          email: '',
          birthDate: '',
          website: ''
        };
        
    
        setTimeout(() => {
          window.location.hash = '#/';
        }, 2000);
      })
      .catch(err => {
        console.error('Erreur:', err);
        this.submitting = false;
        
        if (err.response) {
          if (err.response.status === 409) {
            this.generalError = 'Une invitation ou un CV existe déjà avec cet email';
          } else if (err.response.data && err.response.data.message) {
            this.generalError = err.response.data.message;
          } else {
            this.generalError = 'Erreur lors de l\'envoi de l\'invitation';
          }
        } else {
          this.generalError = 'Erreur de connexion au serveur';
        }
      });
    },
    
    goBack() {
      window.history.back();
    }
  }
};
</script>

<style scoped>
.person-form {
  max-width: 800px;
  margin: 20px auto;
}

.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  display: block;
}

.form-label {
  font-weight: 500;
  color: #333;
}

.text-danger {
  color: #dc3545;
}
</style>
