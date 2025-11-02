import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create an axios instance and attach JWT from localStorage if present
const api = axios.create({ baseURL: API_BASE_URL });
api.interceptors.request.use((config) => {
    const token = (typeof sessionStorage !== 'undefined' && sessionStorage.getItem('token'))
        || (typeof localStorage !== 'undefined' && localStorage.getItem('token'))
        || null;
    if (token) {
        config.headers = config.headers || {};
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

const subscriptionService = {
    // Get all subscription requests (admin)
    getAllRequests: async () => {
        try {
            const response = await api.get(`/subscriptions`);
            return response.data;
        } catch (error) {
            console.error('Error fetching subscription requests:', error);
            throw error;
        }
    },

    // Get pending requests (admin)
    getPendingRequests: async () => {
        try {
            const response = await api.get(`/subscriptions/pending`);
            return response.data;
        } catch (error) {
            console.error('Error fetching pending requests:', error);
            throw error;
        }
    },

    // Get requests by user email (user)
    getRequestsByUser: async (email) => {
        try {
            const response = await api.get(`/subscriptions/user/${encodeURIComponent(email)}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching user requests:', error);
            throw error;
        }
    },

    // Create new subscription request (user)
    createRequest: async (requestData) => {
        try {
            const response = await api.post(`/subscriptions`, requestData);
            return response.data;
        } catch (error) {
            console.error('Error creating subscription request:', error);
            if (error.response && error.response.data) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    },

    // Approve request (admin)
    approveRequest: async (requestId) => {
        try {
            const response = await api.put(`/subscriptions/${requestId}/approve`);
            return response.data;
        } catch (error) {
            console.error('Error approving request:', error);
            if (error.response && error.response.data) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    },

    // Reject request (admin)
    rejectRequest: async (requestId, rejectionReason) => {
        try {
            const response = await api.put(
                `/subscriptions/${requestId}/reject`,
                { rejectionReason }
            );
            return response.data;
        } catch (error) {
            console.error('Error rejecting request:', error);
            if (error.response && error.response.data) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    },

    // Review request with decision (admin)
    reviewRequest: async (requestId, action, rejectionReason = null) => {
        try {
            const response = await api.put(
                `/subscriptions/${requestId}/review`,
                { action, rejectionReason }
            );
            return response.data;
        } catch (error) {
            console.error('Error reviewing request:', error);
            if (error.response && error.response.data) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    },

    // Cancel request (user)
    cancelRequest: async (requestId, userEmail) => {
        try {
            await api.delete(`/subscriptions/${requestId}/cancel?userEmail=${encodeURIComponent(userEmail)}`);
        } catch (error) {
            console.error('Error cancelling request:', error);
            if (error.response && error.response.data) {
                throw new Error(error.response.data);
            }
            throw error;
        }
    }
};

export default subscriptionService;
