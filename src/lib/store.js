export class Store {
  constructor() {
    this.listeners = new Map()
    this.data = this.loadData()
  }

  loadData() {
    try {
      const stored = localStorage.getItem('prizz_db')
      if (stored) return JSON.parse(stored)
    } catch {}
    return {
      requests: [],
      bids: [],
      bookings: [],
      payments: [],
      messages: [],
      notifications: [],
      provider_profiles: [],
      proof_submissions: [],
      ratings: [],
    }
  }

  persist() {
    localStorage.setItem('prizz_db', JSON.stringify(this.data))
    this.emit('change')
  }

  subscribe(callback) {
    const id = Math.random().toString(36)
    this.listeners.set(id, callback)
    return () => this.listeners.delete(id)
  }

  emit(event) {
    for (const listener of this.listeners.values()) {
      listener(event)
    }
  }

  getTable(table) {
    return this.data[table] || []
  }

  setTable(table, rows) {
    this.data[table] = rows
    this.persist()
  }

  insert(table, row) {
    const id = row.id || crypto.randomUUID()
    const newRow = { ...row, id, created_at: new Date().toISOString() }
    this.data[table] = [...(this.data[table] || []), newRow]
    this.persist()
    return newRow
  }

  update(table, id, updates) {
    this.data[table] = (this.data[table] || []).map((row) =>
      row.id === id ? { ...row, ...updates } : row
    )
    this.persist()
  }

  delete(table, id) {
    this.data[table] = (this.data[table] || []).filter((row) => row.id !== id)
    this.persist()
  }

  clearAll() {
    this.data = {
      requests: [],
      bids: [],
      bookings: [],
      payments: [],
      messages: [],
      notifications: [],
      provider_profiles: [],
      proof_submissions: [],
      ratings: [],
    }
    this.persist()
  }
}

export const dbStore = new Store()
