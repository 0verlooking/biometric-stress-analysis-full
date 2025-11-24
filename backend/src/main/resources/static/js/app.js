// API Base URL
const API_BASE = 'http://localhost:8080/api';

// State management
let authToken = localStorage.getItem('authToken');
let currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null');

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    if (authToken && currentUser) {
        showScreen('dashboard-screen');
        loadDashboardData();
    } else {
        showScreen('login-screen');
    }

    // Setup event listeners
    setupEventListeners();
});

function setupEventListeners() {
    // Login form
    document.getElementById('login-form')?.addEventListener('submit', handleLogin);

    // Register form
    document.getElementById('register-form')?.addEventListener('submit', handleRegister);

    // Biometric data form
    document.getElementById('biometric-form')?.addEventListener('submit', handleBiometricSubmit);
}

// Screen navigation
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });
    document.getElementById(screenId)?.classList.add('active');
}

// Notification system
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.classList.add('show');

    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// Authentication
async function handleLogin(e) {
    e.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        });

        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data.user;

            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            showNotification('Login successful!', 'success');
            showScreen('dashboard-screen');
            loadDashboardData();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Login failed', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showNotification('Connection error. Please try again.', 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();

    const registerData = {
        username: document.getElementById('reg-username').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value,
        firstName: document.getElementById('reg-firstname').value,
        lastName: document.getElementById('reg-lastname').value,
        gender: document.getElementById('reg-gender').value || null,
    };

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerData),
        });

        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data.user;

            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            showNotification('Registration successful!', 'success');
            showScreen('dashboard-screen');
            loadDashboardData();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Registration failed', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showNotification('Connection error. Please try again.', 'error');
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    showScreen('login-screen');
    showNotification('Logged out successfully', 'info');
}

// Dashboard
async function loadDashboardData() {
    if (!currentUser) return;

    try {
        // Load biometric data count
        const biometricCount = await apiGet(`/biometric-data/user/${currentUser.id}/count`);
        document.getElementById('total-biometric').textContent = biometricCount;

        // Load stress analysis count
        const analysisCount = await apiGet(`/stress-analysis/user/${currentUser.id}/count`);
        document.getElementById('total-analyses').textContent = analysisCount;

        // Load average stress score (last 30 days)
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
        const avgScore = await apiGet(
            `/stress-analysis/user/${currentUser.id}/average-score?startDate=${thirtyDaysAgo.toISOString()}`
        );
        document.getElementById('avg-stress').textContent = avgScore ? avgScore.toFixed(1) : '--';

        // Load latest analysis
        const analyses = await apiGet(`/stress-analysis/user/${currentUser.id}`);
        if (analyses && analyses.length > 0) {
            displayCurrentStress(analyses[0]);
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

function displayCurrentStress(analysis) {
    const stressDisplay = document.getElementById('current-stress');
    const levelClass = analysis.stressLevel.toLowerCase();
    const percentage = analysis.stressScore;

    stressDisplay.innerHTML = `
        <div class="stress-level ${levelClass}">
            ${analysis.stressLevel} (${analysis.stressScore.toFixed(1)}/100)
        </div>
        <div class="stress-bar">
            <div class="stress-bar-fill" style="width: ${percentage}%; background: ${getStressColor(percentage)};"></div>
        </div>
        <p>Last measured: ${new Date(analysis.createdAt).toLocaleString()}</p>
    `;
}

function getStressColor(score) {
    if (score < 25) return '#28a745';
    if (score < 50) return '#ffc107';
    if (score < 75) return '#fd7e14';
    return '#dc3545';
}

// Biometric data submission
async function handleBiometricSubmit(e) {
    e.preventDefault();

    const biometricData = {
        userId: currentUser.id,
        measurementTime: new Date().toISOString(),
        heartRate: parseInt(document.getElementById('heart-rate').value),
        systolicPressure: parseInt(document.getElementById('systolic').value),
        diastolicPressure: parseInt(document.getElementById('diastolic').value),
        bodyTemperature: parseFloat(document.getElementById('temperature').value),
        sleepHours: document.getElementById('sleep-hours').value ?
            parseFloat(document.getElementById('sleep-hours').value) : null,
        sleepQuality: document.getElementById('sleep-quality').value || null,
        respiratoryRate: document.getElementById('respiratory-rate').value ?
            parseInt(document.getElementById('respiratory-rate').value) : null,
        oxygenSaturation: document.getElementById('oxygen').value ?
            parseFloat(document.getElementById('oxygen').value) : null,
    };

    try {
        // Save biometric data
        const savedData = await apiPost('/biometric-data', biometricData);

        showNotification('Biometric data saved!', 'success');

        // Analyze stress
        const analysis = await apiPost(`/stress-analysis/analyze/${savedData.id}`, {});

        // Show results
        displayAnalysisResults(analysis);
        showScreen('results-screen');
    } catch (error) {
        console.error('Error submitting biometric data:', error);
        showNotification('Failed to save data. Please try again.', 'error');
    }
}

function displayAnalysisResults(analysis) {
    const resultsDiv = document.getElementById('analysis-results');
    const levelClass = analysis.stressLevel.toLowerCase();

    let recommendationsHTML = '';
    if (analysis.recommendations && analysis.recommendations.length > 0) {
        recommendationsHTML = '<div class="recommendations"><h3>💡 Recommendations</h3>';
        analysis.recommendations.forEach(rec => {
            recommendationsHTML += `
                <div class="recommendation-item priority-${rec.priority.toLowerCase()}">
                    <h4>${rec.category}: ${rec.title}</h4>
                    <p>${rec.description}</p>
                    <small>Priority: ${rec.priority}</small>
                </div>
            `;
        });
        recommendationsHTML += '</div>';
    }

    resultsDiv.innerHTML = `
        <div class="stress-display">
            <div class="stress-level ${levelClass}">
                Overall Stress Level: ${analysis.stressLevel}
            </div>
            <div class="stress-level ${levelClass}">
                Stress Score: ${analysis.stressScore.toFixed(1)} / 100
            </div>
            <div class="stress-bar">
                <div class="stress-bar-fill" style="width: ${analysis.stressScore}%; background: ${getStressColor(analysis.stressScore)};"></div>
            </div>
        </div>

        <div class="card">
            <h3>📈 Component Scores</h3>
            <p>Cardiovascular: ${analysis.cardiovascularScore?.toFixed(1) || 'N/A'} (Weight: 30%)</p>
            <p>Thermal: ${analysis.thermalScore?.toFixed(1) || 'N/A'} (Weight: 15%)</p>
            <p>Biochemical: ${analysis.biochemicalScore?.toFixed(1) || 'N/A'} (Weight: 25%)</p>
            <p>Sleep: ${analysis.sleepScore?.toFixed(1) || 'N/A'} (Weight: 20%)</p>
            <p>Respiratory: ${analysis.respiratoryScore?.toFixed(1) || 'N/A'} (Weight: 10%)</p>
        </div>

        <div class="card">
            <h3>📝 Analysis</h3>
            <p>${analysis.analysis}</p>
        </div>

        ${recommendationsHTML}
    `;
}

// API helper functions
async function apiGet(endpoint) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
        headers: {
            'Authorization': `Bearer ${authToken}`,
        },
    });

    if (!response.ok) {
        throw new Error('API request failed');
    }

    return response.json();
}

async function apiPost(endpoint, data) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authToken}`,
        },
        body: JSON.stringify(data),
    });

    if (!response.ok) {
        throw new Error('API request failed');
    }

    return response.json();
}
