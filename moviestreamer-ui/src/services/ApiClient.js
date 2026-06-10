const BASE_URL = 'http://localhost:8888/api'

class ApiClient {
    async request(endpoint, options = {}) {
        const url = `${BASE_URL}${endpoint}`

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        }

        let response

        try {
            response = await fetch(url, {
                ...options,
                headers
            })
        } catch (networkError) {
            console.error(`[API] Network error on ${options.method ?? 'GET'} ${url}:`, networkError.message)
            throw networkError
        }

        if (!response.ok) {
            let errorBody = null

            try {
                errorBody = await response.json()
            } catch {
                errorBody = await response.text().catch(() => null)
            }

            const error = new Error(`API Error ${response.status}: ${response.statusText}`)
            error.status = response.status
            error.endpoint = endpoint
            error.body = errorBody

            console.error(
                `[API] ${options.method ?? 'GET'} ${url} failed with ${response.status} ${response.statusText}`,
                ...(errorBody ? ['→ Response body:', errorBody] : [])
            )

            throw error
        }

        if (response.status === 204) {
            return null
        }

        try {
            return await response.json()
        } catch (parseError) {
            console.error(`[API] Failed to parse JSON response from ${url}:`, parseError.message)
            throw parseError
        }
    }

    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' })
    }

    async post(endpoint, data) {
        return this.request(endpoint, { method: 'POST', body: JSON.stringify(data) })
    }

    async put(endpoint, data) {
        return this.request(endpoint, { method: 'PUT', body: JSON.stringify(data) })
    }

    async patch(endpoint, data) {
        return this.request(endpoint, { method: 'PATCH', body: JSON.stringify(data) })
    }

    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' })
    }
}

export default new ApiClient()