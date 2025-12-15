<template>
  <div>
    <h2><i class="bi bi-search"></i> Rechercher des CVs</h2>
    
    <div class="card mb-4">
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Recherche par nom</label>
            <input 
              type="text" 
              class="form-control" 
              placeholder="Nom ou prénom..."
              v-model="nameQuery"
              @input="onNameInput"
            />
          </div>
          <div class="col-md-6">
            <label class="form-label">Recherche par activité</label>
            <input 
              type="text" 
              class="form-control" 
              placeholder="Titre d'activité..."
              v-model="activityQuery"
              @input="onActivityInput"
            />
          </div>
        </div>
      </div>
    </div>

    <div v-if="searching" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Recherche en cours...</span>
      </div>
    </div>

    <div v-else-if="results.length === 0 && (nameQuery || activityQuery)" class="alert alert-info">
      <i class="bi bi-info-circle"></i> Aucun résultat trouvé.
    </div>

    <div v-else-if="results.length > 0">
      <h4>{{ results.length }} résultat(s) trouvé(s)</h4>
      <div class="row">
        <div v-for="person in results" :key="person.id" class="col-md-6 col-lg-4 mb-4">
          <div class="card h-100 shadow-sm person-card" @click="viewPerson(person.id)">
            <div class="card-body">
              <div class="d-flex align-items-center mb-3">
                <div class="avatar-circle me-3">
                  {{ getInitials(person.firstName, person.lastName) }}
                </div>
                <div>
                  <h5 class="card-title mb-0">{{ person.firstName }} {{ person.lastName }}</h5>
                  <p class="text-muted small mb-0">
                    <i class="bi bi-envelope"></i> {{ person.email }}
                  </p>
                </div>
              </div>
              
              <div class="mb-2">
                <span class="badge bg-info">
                  <i class="bi bi-briefcase"></i> 
                  {{ person.activities ? person.activities.length : 0 }} activité(s)
                </span>
              </div>

              <button class="btn btn-sm btn-primary mt-2" @click.stop="viewPerson(person.id)">
                <i class="bi bi-eye"></i> Voir le CV
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center text-muted my-5">
      <i class="bi bi-search" style="font-size: 3rem;"></i>
      <p class="mt-3">Utilisez les champs ci-dessus pour rechercher des CVs</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      nameQuery: '',
      activityQuery: '',
      results: [],
      searching: false,
      axiosInstance: null,
      nameTimeout: null,
      activityTimeout: null
    };
  },
  mounted() {
    this.axiosInstance = axios.create({
      baseURL: '/api/',
      headers: {
        'Content-Type': 'application/json'
      }
    });
  },
  methods: {
    onNameInput() {
      clearTimeout(this.nameTimeout);
      this.nameTimeout = setTimeout(() => {
        this.searchByName();
      }, 500);
    },
    onActivityInput() {
      clearTimeout(this.activityTimeout);
      this.activityTimeout = setTimeout(() => {
        this.searchByActivity();
      }, 500);
    },
    searchByName() {
      if (!this.nameQuery.trim()) {
        this.results = [];
        return;
      }
      this.searching = true;
      this.activityQuery = '';
      this.axiosInstance.get(`persons/search?q=${this.nameQuery}`)
        .then(response => {
          this.results = response.data.content;
          this.searching = false;
        })
        .catch(err => {
          console.error('Erreur de recherche:', err);
          this.searching = false;
          alert('Erreur lors de la recherche');
        });
    },
    
    searchByActivity() {
      if (!this.activityQuery.trim()) {
        this.results = [];
        return;
      }
      this.searching = true;
      this.nameQuery = '';
      this.axiosInstance.get(`persons/search-by-activity?title=${this.activityQuery}`)
        .then(response => {
          this.results = response.data.content;
          this.searching = false;
        })
        .catch(err => {
          console.error('Erreur de recherche:', err);
          this.searching = false;
          alert('Erreur lors de la recherche');
        });
    },
    
    viewPerson(id) {
      window.location.hash = `#/person/${id}`;
    },
    
    getInitials(firstName, lastName) {
      return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
    }
  }
};
</script>

<style scoped>
.person-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.person-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2) !important;
}

.avatar-circle {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
}
</style>
