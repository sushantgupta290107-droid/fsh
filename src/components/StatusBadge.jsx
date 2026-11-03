const map = {
  open: { label: 'OPEN', className: 'bg-paper border-ink' },
  bidding: { label: 'BIDDING', className: 'bg-signal/30 border-ink' },
  provider_selected: { label: 'SELECTED', className: 'bg-interactive/20 border-ink' },
  payment_pending: { label: 'PAYMENT', className: 'bg-signal/40 border-ink' },
  confirmed: { label: 'CONFIRMED', className: 'bg-success/20 border-ink' },
  in_progress: { label: 'IN PROGRESS', className: 'bg-paper border-prizz' },
  proof_submitted: { label: 'PROOF', className: 'bg-signal/30 border-ink' },
  completed: { label: 'DONE', className: 'bg-success text-paper border-ink' },
  cancelled: { label: 'CANCELLED', className: 'bg-stone/20 border-ink' },
  disputed: { label: 'DISPUTED', className: 'bg-prizz text-paper border-ink' },
}

export default function StatusBadge({ status }) {
  const cfg = map[status] ?? { label: status, className: 'bg-paper border-ink' }
  return (
    <span
      className={`inline-block text-[10px] font-black uppercase px-2 py-1 border-2 tracking-wider ${cfg.className}`}
    >
      {cfg.label}
    </span>
  )
}
