import { useEffect, useState, useCallback } from 'react'
import { fetchCustomerRequests } from '../lib/api'
import { DEMO_CUSTOMER_ID } from '../lib/constants'
import { dbStore } from '../lib/store'

export function useRequests(customerId = DEMO_CUSTOMER_ID) {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const reload = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchCustomerRequests(customerId)
      setRequests(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [customerId])

  useEffect(() => {
    reload()
  }, [reload])

  useEffect(() => {
    return dbStore.subscribe(() => reload())
  }, [reload])

  return { requests, loading, error, reload }
}
