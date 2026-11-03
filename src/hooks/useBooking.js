import { useEffect, useState, useCallback } from 'react'
import { fetchBooking } from '../lib/api'
import { dbStore } from '../lib/store'

export function useBooking(bookingId) {
  const [booking, setBooking] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const reload = useCallback(async () => {
    if (!bookingId) return
    setLoading(true)
    try {
      const data = await fetchBooking(bookingId)
      setBooking(data)
    } catch (e) {
      setError(e.message)
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

  return { booking, loading, error, reload }
}
