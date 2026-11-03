export default function OfferBadge({ children = 'NEW OFFER' }) {
  return (
    <span className="inline-block bg-signal text-ink text-xs font-black uppercase px-2 py-1 border-2 border-ink tracking-wide">
      {children}
    </span>
  )
}
