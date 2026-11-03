import { useState } from 'react'
import { sendMessage } from '../lib/api'
import { useMessages } from '../hooks/useMessages'

export default function ChatPanel({ bookingId, senderId }) {
  const { messages } = useMessages(bookingId)
  const [text, setText] = useState('')
  const [sending, setSending] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    if (!text.trim()) return
    setSending(true)
    try {
      await sendMessage({ bookingId, senderId, body: text.trim() })
      setText('')
    } finally {
      setSending(false)
    }
  }

  return (
    <div className="panel-border flex flex-col h-80">
      <div className="border-b-2 border-ink px-4 py-2 font-bold uppercase text-sm">Chat</div>
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {messages.map((m) => (
          <div
            key={m.id}
            className={`max-w-[85%] px-3 py-2 border-2 border-ink text-sm ${
              m.sender_id === senderId ? 'ml-auto bg-signal/30' : 'bg-paper'
            }`}
          >
            <p className="text-[10px] uppercase text-stone mb-1">{m.sender_id}</p>
            {m.body}
          </div>
        ))}
        {!messages.length && <p className="text-stone text-sm">No messages yet.</p>}
      </div>
      <form onSubmit={submit} className="border-t-2 border-ink flex">
        <input
          className="flex-1 px-3 py-2 bg-paper outline-none text-sm"
          placeholder="Type a message..."
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
        <button type="submit" disabled={sending} className="px-4 font-bold uppercase bg-ink text-paper text-sm">
          Send
        </button>
      </form>
    </div>
  )
}
