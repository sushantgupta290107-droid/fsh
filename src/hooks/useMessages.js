import { useEffect, useState, useCallback } from 'react'
import { fetchMessages } from '../lib/api'
import { dbStore } from '../lib/store'

export function useMessages(bookingId) {
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(true)

  const reload = useCallback(async () => {
    if (!bookingId) return
    try {
      const data = await fetchMessages(bookingId)
      setMessages(data)
    } finally {
      setLoading(false)
    }
  }, [bookingId])

  useEffect(() => {
    reload()
  }, [reload])

  useEffect(() => {
    if (!bookingId) return
    return dbStore.subscribe(() => reload())
  }, [bookingId, reload])

  return { messages, loading, reload }
}
