import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AppShell from '../components/AppShell'
import { REQUEST_CATEGORIES } from '../lib/constants'
import { createRequest } from '../lib/api'

const empty = {
  category: '',
  description: '',
  title: '',
  fullDescription: '',
  quantity: '',
  scheduled_date: '',
  start_time: '',
  duration_hours: '',
  location: '',
  image_url: '',
  budget: '',
  let_providers_set_price: false,
  urgent: false,
}

export default function RequestComposer() {
  const [step, setStep] = useState(1)
  const [form, setForm] = useState(empty)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const navigate = useNavigate()

  const set = (key, val) => setForm((f) => ({ ...f, [key]: val }))

  const next = () => setStep((s) => Math.min(4, s + 1))
  const back = () => setStep((s) => Math.max(1, s - 1))

  const post = async () => {

    setSubmitting(true)
    setError(null)
    try {
      const req = await createRequest({
        category: form.category,
        title: form.title,
        description: form.fullDescription || form.description,
        quantity: form.quantity ? Number(form.quantity) : null,
        scheduled_date: form.scheduled_date || null,
        start_time: form.start_time || null,
        duration_hours: form.duration_hours ? Number(form.duration_hours) : null,
        location: form.location,
        image_url: form.image_url || null,
        budget: form.let_providers_set_price ? null : Number(form.budget) || null,
        let_providers_set_price: form.let_providers_set_price,
        urgent: form.urgent,
      })
      navigate(`/live/${req.id}`)
    } catch (e) {
      setError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AppShell>
      <div className="min-h-[calc(100vh-4rem)] max-w-2xl mx-auto px-4 py-8 flex flex-col">
        <p className="text-xs font-bold uppercase text-stone mb-2">Step {step} of 4</p>

        {step === 1 && (
          <>
            <h1 className="prizz-display text-5xl mb-6">WHAT DO YOU NEED?</h1>
            <div className="grid grid-cols-2 gap-2 mb-6">
              {REQUEST_CATEGORIES.map((c) => (
                <button
                  key={c}
                  type="button"
                  onClick={() => set('category', c)}
                  className={`text-left px-3 py-3 border-2 border-ink text-sm font-semibold uppercase ${
                    form.category === c ? 'bg-ink text-paper' : 'hover:bg-border/40'
                  }`}
                >
                  {c}
                </button>
              ))}
            </div>
            <label className="block text-sm font-bold uppercase mb-2">Describe what you need…</label>
            <textarea
              className="w-full border-2 border-ink bg-paper p-3 min-h-[100px]"
              value={form.description}
              onChange={(e) => set('description', e.target.value)}
            />
            <div className="mt-auto pt-8 flex justify-end">
              <button type="button" className="btn-primary" disabled={!form.category} onClick={next}>
                Continue
              </button>
            </div>
          </>
        )}

        {step === 2 && (
          <>
            <h1 className="prizz-display text-4xl mb-6">DETAILS</h1>
            <div className="space-y-4">
              {[
                ['title', 'Title', 'text'],
                ['fullDescription', 'Description', 'textarea'],
                ['quantity', 'Quantity', 'number'],
                ['scheduled_date', 'Date', 'date'],
                ['start_time', 'Start time', 'time'],
                ['duration_hours', 'Duration (hours)', 'number'],
                ['location', 'Location', 'text'],
                ['image_url', 'Image URL (optional)', 'text'],
              ].map(([key, label, type]) =>
                type === 'textarea' ? (
                  <label key={key} className="block">
                    <span className="text-xs font-bold uppercase">{label}</span>
                    <textarea
                      className="mt-1 w-full border-2 border-ink p-2 bg-paper"
                      value={form[key]}
                      onChange={(e) => set(key, e.target.value)}
                    />
                  </label>
                ) : (
                  <label key={key} className="block">
                    <span className="text-xs font-bold uppercase">{label}</span>
                    <input
                      type={type}
                      className="mt-1 w-full border-2 border-ink p-2 bg-paper"
                      value={form[key]}
                      onChange={(e) => set(key, e.target.value)}
                    />
                  </label>
                ),
              )}
            </div>
            <div className="mt-8 flex justify-between">
              <button type="button" className="btn-secondary" onClick={back}>
                Back
              </button>
              <button type="button" className="btn-primary" onClick={next}>
                Continue
              </button>
            </div>
          </>
        )}

        {step === 3 && (
          <>
            <h1 className="prizz-display text-4xl mb-2">BUDGET</h1>
            <label className="block mb-6">
              <span className="prizz-display text-5xl">₹</span>
              <input
                type="number"
                disabled={form.let_providers_set_price}
                className="ml-2 border-b-4 border-ink bg-transparent text-4xl font-display w-48 outline-none"
                value={form.budget}
                onChange={(e) => set('budget', e.target.value)}
              />
            </label>
            <label className="flex items-center gap-3 mb-4 cursor-pointer">
              <input
                type="checkbox"
                checked={form.let_providers_set_price}
                onChange={(e) => set('let_providers_set_price', e.target.checked)}
              />
              <span className="font-bold uppercase text-sm">Let providers set the price</span>
            </label>
            <label className="flex items-center gap-3 cursor-pointer">
              <input type="checkbox" checked={form.urgent} onChange={(e) => set('urgent', e.target.checked)} />
              <span className="font-bold uppercase text-sm text-prizz">Urgent</span>
            </label>
            <div className="mt-8 flex justify-between">
              <button type="button" className="btn-secondary" onClick={back}>
                Back
              </button>
              <button type="button" className="btn-primary" onClick={next}>
                Review
              </button>
            </div>
          </>
        )}

        {step === 4 && (
          <>
            <h2 className="prizz-display text-4xl mb-6">REVIEW</h2>
            <div className="panel-border p-6 space-y-3">
              <h3 className="font-bold text-2xl uppercase">{form.title || form.description.slice(0, 40)}</h3>
              <p className="text-stone">
                {form.scheduled_date} · {form.start_time}
                {form.duration_hours ? ` · ${form.duration_hours}h` : ''}
              </p>
              <p className="font-semibold">{form.location}</p>
              <p className="text-sm">{form.fullDescription || form.description}</p>
              <p className="font-display text-3xl">
                {form.let_providers_set_price ? 'Market sets price' : `Budget ₹${form.budget}`}
              </p>
              {form.urgent && <p className="text-prizz font-bold uppercase text-sm">Urgent</p>}
            </div>
            {error && <p className="text-prizz mt-4 text-sm">{error}</p>}
            <div className="mt-8 flex flex-col sm:flex-row gap-3">
              <button type="button" className="btn-secondary" onClick={back}>
                Back
              </button>
              <button type="button" className="btn-primary flex-1 text-lg" disabled={submitting} onClick={post}>
                Post request
              </button>
            </div>
          </>
        )}
      </div>
    </AppShell>
  )
}
