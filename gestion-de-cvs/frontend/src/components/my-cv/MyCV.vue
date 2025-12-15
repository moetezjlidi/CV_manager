<template>
  <div>
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Chargement...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
    </div>

    <div v-else-if="!person" class="alert alert-warning">
      <i class="bi bi-exclamation-triangle"></i> 
      <strong>Votre CV n'existe pas encore.</strong>
      <br/><br/>
      <p>Cliquez sur le bouton ci-dessous pour créer automatiquement votre CV :</p>
      <button @click="createMyCV" class="btn btn-primary mt-3" :disabled="creatingCV">
        <span v-if="creatingCV" class="spinner-border spinner-border-sm me-2" role="status"></span>
        <i v-else class="bi bi-plus-circle me-2"></i>
        {{ creatingCV ? 'Création en cours...' : 'Créer mon CV' }}
      </button>
      <div v-if="createError" class="alert alert-danger mt-3">
        <i class="bi bi-x-circle"></i> {{ createError }}
      </div>
    </div>

    <div v-else class="person-details">
      <div class="card shadow">
        <!-- En-tête du CV -->
        <div class="card-header bg-gradient-primary text-white">
          <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
              <div class="avatar-large me-3">
                {{ getInitials(person.firstName, person.lastName) }}
              </div>
              <div>
                <h2 class="mb-0">{{ person.firstName }} {{ person.lastName }}</h2>
                <p class="mb-0"><i class="bi bi-envelope"></i> {{ person.email }}</p>
              </div>
            </div>
            <div>
              <a href="#/my-cv/edit" class="btn btn-light me-2">
                <i class="bi bi-pencil"></i> Modifier
              </a>
              <a href="#/my-cv/activities" class="btn btn-success">
                <i class="bi bi-briefcase"></i> Activités
              </a>
            </div>
          </div>
        </div>
        
        <!-- Informations personnelles -->
        <div class="card-body">
          <div class="row mb-4">
            <div class="col-md-6">
              <h5><i class="bi bi-person-badge"></i> Informations Personnelles</h5>
              <ul class="list-unstyled">
                <li><strong>Nom :</strong> {{ person.lastName }}</li>
                <li><strong>Prénom :</strong> {{ person.firstName }}</li>
                <li><strong>Email :</strong> {{ person.email }}</li>
                <li v-if="person.birthDate">
                  <strong>Date de naissance :</strong> {{ formatDate(person.birthDate) }}
                </li>
                <li v-if="person.website">
                  <strong>Site Web :</strong> 
                  <a :href="person.website" target="_blank" rel="noopener noreferrer">
                    {{ person.website }}
                  </a>
                </li>
              </ul>
            </div>
            
            <div class="col-md-6">
              <h5><i class="bi bi-graph-up"></i> Statistiques</h5>
              <div class="stats">
                <div class="stat-item">
                  <div class="stat-number">{{ getTotalActivities() }}</div>
                  <div class="stat-label">Activités au total</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('EXPERIENCE_PROFESSIONAL') }}</div>
                  <div class="stat-label">Expériences Pro</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('TRAINING') }}</div>
                  <div class="stat-label">Formations</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ getActivitiesByNature('PROJECTS') }}</div>
                  <div class="stat-label">Projets</div>
                </div>
              </div>
            </div>
          </div>
          

          <div class="activities-section">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4><i class="bi bi-briefcase-fill"></i> Activités & Parcours</h4>
              <a href="#/my-cv/activities" class="btn btn-sm btn-success">
                <i class="bi bi-plus-circle"></i> Gérer mes activités
              </a>
            </div>
            
            <div v-if="!person.activities || person.activities.length === 0" class="alert alert-info">
              <i class="bi bi-info-circle"></i> Aucune activité enregistrée.
            </div>
            
            <div v-else class="timeline">
              <div 
                v-for="activity in sortedActivities" 
                :key="activity.id" 
                class="timeline-item"
              >
                <div class="timeline-marker" :class="getNatureClass(activity.nature)"></div>
                <div class="timeline-content card mb-3">
                  <div class="card-body">
                    <h5 class="card-title">
                      <span class="badge" :class="getNatureBadgeClass(activity.nature)">
                        {{ getNatureLabel(activity.nature) }}
                      </span>
                      {{ activity.title }}
                    </h5>
                    <p class="text-muted mb-2">
                      <i class="bi bi-calendar3"></i> {{ activity.activityYear }}
                    </p>
                    <p class="card-text" v-if="activity.description">
                      {{ activity.description }}
                    </p>
                    <div v-if="activity.webAddress" class="mt-2">
                      <a :href="activity.webAddress" target="_blank" rel="noopener noreferrer" class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-link-45deg"></i> Voir en ligne
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
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
      person: null,
      loading: true,
      error: null,
      creatingCV: false,
      createError: null
    };
  },
  computed: {
    sortedActivities() {
      if (!this.person || !this.person.activities) return [];
      return [...this.person.activities].sort((a, b) => b.activityYear - a.activityYear);
    }
  },
  mounted() {
    this.loadMyCV();
  },
  methods: {
    loadMyCV() {
      this.loading = true;
      this.error = null;
      axios.get('/api/persons/me', {
        headers: {
          'Authorization': `Bearer ${this.user.token}`
        }
      })
      .then(response => {
        this.person = response.data;
        this.loading = false;
      })
      .catch(err => {
        console.error('Erreur:', err);
        this.error = 'Impossible de charger votre CV';
        this.loading = false;
      });
    },
    
    createMyCV() {
      this.creatingCV = true;
      this.createError = null;
      
      const token = this.user.token;
      
      if (!token) {
        this.createError = 'Erreur: Vous devez être connecté pour créer votre CV.';
        this.creatingCV = false;
        return;
      }
      
      axios.post('/api/persons/create-my-cv', {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        this.loadMyCV();
        this.creatingCV = false;
      })
      .catch(err => {
        console.error('Erreur lors de la création du CV:', err);
        this.createError = err.response?.data?.message || 'Impossible de créer votre CV automatiquement. Erreur: ' + err.message;
        this.creatingCV = false;
      });
    },
    
    getInitials(firstName, lastName) {
      return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' });
    },
    
    getTotalActivities() {
      return this.person && this.person.activities ? this.person.activities.length : 0;
    },
    
    getActivitiesByNature(nature) {
      if (!this.person || !this.person.activities) return 0;
      return this.person.activities.filter(a => a.nature === nature).length;
    },
    
    getNatureLabel(nature) {
      const labels = {
        'EXPERIENCE_PROFESSIONAL': '💼 Expérience Pro',
        'TRAINING': '🎓 Formation',
        'PROJECTS': '🚀 Projet',
        'OTHER': '📌 Autre'
      };
      return labels[nature] || nature;
    },
    
    getNatureBadgeClass(nature) {
      const classes = {
        'EXPERIENCE_PROFESSIONAL': 'bg-primary',
        'TRAINING': 'bg-success',
        'PROJECTS': 'bg-warning text-dark',
        'OTHER': 'bg-secondary'
      };
      return classes[nature] || 'bg-secondary';
    },
    
    getNatureClass(nature) {
      const classes = {
        'EXPERIENCE_PROFESSIONAL': 'marker-primary',
        'TRAINING': 'marker-success',
        'PROJECTS': 'marker-warning',
        'OTHER': 'marker-secondary'
      };
      return classes[nature] || 'marker-secondary';
    }
  }
};
</script>

<style scoped>
.avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 2rem;
}

.bg-gradient-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  font-size: 0.85rem;
  color: #6c757d;
}

.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-marker {
  position: absolute;
  left: -30px;
  top: 10px;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 0 0 2px #667eea;
}

.marker-primary { box-shadow: 0 0 0 2px #0d6efd; }
.marker-success { box-shadow: 0 0 0 2px #198754; }
.marker-warning { box-shadow: 0 0 0 2px #ffc107; }
.marker-secondary { box-shadow: 0 0 0 2px #6c757d; }

.timeline::before {
  content: '';
  position: absolute;
  left: -23px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e9ecef;
}
</style>
