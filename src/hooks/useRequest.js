import { useEffect, useState, useCallback } from 'react'
import { fetchRequest } from '../lib/api'
import { dbStore } from '../lib/store'

export function useRequest(requestId) {
  const [request, setRequest] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const reload = useCallback(async () => {
    if (!requestId) return
    setLoading(true)
    setError(null)
    try {
      const data = await fetchRequest(requestId)
      setRequest(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [requestId])

  useEffect(() => {
    reload()
  }, [reload])

  useEffect(() => {
    if (!requestId) return
    return dbStore.subscribe(() => reload())
  }, [requestId, reload])

  return { request, loading, error, reload }
}
