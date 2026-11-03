import { Link } from 'react-router-dom'
import StatusBadge from './StatusBadge'

export default function RequestCard({ request, to }) {
  const link = to ?? (request.status === 'bidding' || request.status === 'open' ? `/live/${request.id}` : `/customer`)

  return (
    <Link
      to={link}
      className="block panel-border p-4 hover:bg-border/20 transition-colors"
    >
      <div className="flex justify-between gap-3 items-start">
        <h3 className="font-bold uppercase text-lg leading-tight">{request.title}</h3>
        <StatusBadge status={request.status} />
      </div>
      <p className="text-sm text-stone mt-2">{request.location}</p>
      {request.budget && (
        <p className="mt-3 font-display text-2xl">₹{Number(request.budget).toLocaleString('en-IN')}</p>
      )}
    </Link>
  )
}
