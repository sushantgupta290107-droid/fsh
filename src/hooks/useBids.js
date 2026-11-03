import { useEffect, useState, useCallback } from 'react'
import { fetchBidsForRequest, markBidSeen } from '../lib/api'
import { dbStore } from '../lib/store'

export function useBids(requestId) {
  const [bids, setBids] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const reload = useCallback(async () => {
    if (!requestId) return
    setLoading(true)
    setError(null)
    try {
      const data = await fetchBidsForRequest(requestId)
      setBids(data)
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

  const acknowledgeNew = useCallback(async (bidId) => {
    await markBidSeen(bidId)
    setBids((prev) => prev.map((b) => (b.id === bidId ? { ...b, is_new: false } : b)))
  }, [])

  return { bids, loading, error, reload, acknowledgeNew }
}
