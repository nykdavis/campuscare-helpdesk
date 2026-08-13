import { useCallback, useEffect, useMemo, useState } from 'react'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const categories = [['IT_SUPPORT','IT support'],['FACILITIES','Facilities'],['ACADEMICS','Academics'],['LIBRARY','Library'],['TRANSPORT','Transport'],['OTHER','Other']]
const categoryLabels = Object.fromEntries(categories)
const statusLabels = { OPEN: 'Open', IN_PROGRESS: 'In progress', CLOSED: 'Closed' }
const emptyForm = { title:'', description:'', category:'IT_SUPPORT', studentName:'', studentEmail:'' }

function Icon({ name, size=20 }) {
  const paths = {
    tickets:<><path d="M4 5a2 2 0 0 1 2-2h12v5a2 2 0 0 0 0 4v5H6a2 2 0 0 1-2-2V5Z"/><path d="M9 7h5M9 11h3"/></>,
    plus:<path d="M12 5v14M5 12h14"/>, search:<><circle cx="11" cy="11" r="7"/><path d="m20 20-4-4"/></>,
    refresh:<><path d="M20 7v5h-5"/><path d="M4 17a8 8 0 0 0 13.5 1M4 17v-5h5M20 7A8 8 0 0 0 6.5 6"/></>,
    trash:<><path d="M4 7h16M9 7V4h6v3M7 7l1 13h8l1-13"/><path d="M10 11v5M14 11v5"/></>, close:<path d="m6 6 12 12M18 6 6 18"/>,
    arrow:<path d="m9 18 6-6-6-6"/>, inbox:<><path d="M4 5h16l2 9h-6l-2 3h-4l-2-3H2l2-9Z"/><path d="M5 9h14"/></>,
    clock:<><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></>, check:<path d="m5 12 4 4L19 6"/>,
    alert:<><circle cx="12" cy="12" r="9"/><path d="M12 8v5M12 16h.01"/></>,
  }
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name]}</svg>
}

function App() {
  const [tickets,setTickets] = useState([]), [loading,setLoading] = useState(true), [error,setError] = useState('')
  const [query,setQuery] = useState(''), [status,setStatus] = useState('ALL'), [category,setCategory] = useState('ALL')
  const [modalOpen,setModalOpen] = useState(false), [form,setForm] = useState(emptyForm), [formErrors,setFormErrors] = useState({})
  const [submitting,setSubmitting] = useState(false), [toast,setToast] = useState('')

  const loadTickets = useCallback(async()=>{setLoading(true);setError('');try{const response=await fetch(`${API_URL}/api/tickets`);if(!response.ok)throw new Error();setTickets(await response.json())}catch{setError('We could not connect to CampusCare. Make sure the backend is running.')}finally{setLoading(false)}},[])
  useEffect(()=>{loadTickets()},[loadTickets])
  useEffect(()=>{if(!toast)return;const timer=setTimeout(()=>setToast(''),3500);return()=>clearTimeout(timer)},[toast])

  const filteredTickets=useMemo(()=>{const search=query.trim().toLowerCase();return tickets.filter(ticket=>(!search||[ticket.title,ticket.studentName,ticket.studentEmail].some(value=>value?.toLowerCase().includes(search)))&&(status==='ALL'||ticket.status===status)&&(category==='ALL'||ticket.category===category))},[tickets,query,status,category])
  const counts=useMemo(()=>({total:tickets.length,open:tickets.filter(t=>t.status==='OPEN').length,progress:tickets.filter(t=>t.status==='IN_PROGRESS').length,closed:tickets.filter(t=>t.status==='CLOSED').length}),[tickets])

  function validate(){const errors={};if(!form.title.trim())errors.title='Please enter a title.';if(!form.description.trim())errors.description='Please describe the issue.';if(!form.studentName.trim())errors.studentName='Please enter your name.';if(!/^\S+@\S+\.\S+$/.test(form.studentEmail))errors.studentEmail='Enter a valid email address.';setFormErrors(errors);return !Object.keys(errors).length}
  async function createTicket(event){event.preventDefault();if(!validate())return;setSubmitting(true);try{const response=await fetch(`${API_URL}/api/tickets`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(form)});const body=await response.json();if(!response.ok){setFormErrors(body.validationErrors||{form:body.message||'Could not create ticket.'});return}setTickets(current=>[body,...current]);setForm(emptyForm);setModalOpen(false);setToast(`Ticket #${body.id} created successfully`)}catch{setFormErrors({form:'Could not connect to the server. Please try again.'})}finally{setSubmitting(false)}}
  async function updateStatus(ticket,nextStatus){if(ticket.status===nextStatus)return;try{const response=await fetch(`${API_URL}/api/tickets/${ticket.id}/status`,{method:'PATCH',headers:{'Content-Type':'application/json'},body:JSON.stringify({status:nextStatus})});if(!response.ok)throw new Error();const updated=await response.json();setTickets(current=>current.map(item=>item.id===updated.id?updated:item));setToast(`Ticket #${ticket.id} moved to ${statusLabels[nextStatus].toLowerCase()}`)}catch{setToast('Status update failed. Please try again.')}}
  async function deleteTicket(ticket){if(!window.confirm(`Delete ticket #${ticket.id}? This cannot be undone.`))return;try{const response=await fetch(`${API_URL}/api/tickets/${ticket.id}`,{method:'DELETE'});if(!response.ok)throw new Error();setTickets(current=>current.filter(item=>item.id!==ticket.id));setToast(`Ticket #${ticket.id} deleted`)}catch{setToast('Ticket could not be deleted.')}}
  const formatDate=value=>value?new Intl.DateTimeFormat('en-IN',{day:'numeric',month:'short',year:'numeric'}).format(new Date(value)):'Today'

  return <div className="app-shell">
    <header className="topbar"><a className="brand" href="#top"><span className="brand-mark"><Icon name="tickets" size={24}/></span><span><strong>CampusCare</strong><small>Student Helpdesk</small></span></a><nav><a className="active" href="#tickets">Tickets</a><a href="#support">Support</a></nav><button className="primary compact" onClick={()=>setModalOpen(true)}><Icon name="plus" size={18}/>New ticket</button></header>
    <main id="top">
      <section className="hero-section"><div><span className="eyebrow">Student support, simplified</span><h1>How can we help<br/>you today?</h1><p>Report campus issues, follow their progress, and get back to what matters.</p><button className="primary hero-button" onClick={()=>setModalOpen(true)}><Icon name="plus"/>Create a ticket</button></div><div className="hero-art" aria-hidden="true"><div className="orb orb-one"/><div className="orb orb-two"/><div className="help-card card-back"><span/><span/><span/></div><div className="help-card card-front"><div className="check-ring"><Icon name="check" size={35}/></div><strong>We’re on it!</strong><small>Your request is in good hands.</small></div></div></section>
      <section className="stats"><Stat icon="inbox" color="blue" value={counts.total} label="Total tickets"/><Stat icon="alert" color="amber" value={counts.open} label="Open"/><Stat icon="clock" color="purple" value={counts.progress} label="In progress"/><Stat icon="check" color="green" value={counts.closed} label="Resolved"/></section>
      <section className="tickets-section" id="tickets"><div className="section-heading"><div><span className="eyebrow">Your requests</span><h2>Helpdesk tickets</h2></div><button className="icon-button" onClick={loadTickets} title="Refresh"><Icon name="refresh"/></button></div>
        <div className="toolbar"><label className="search"><Icon name="search" size={19}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search tickets or students..."/></label><select value={status} onChange={e=>setStatus(e.target.value)}><option value="ALL">All statuses</option>{Object.entries(statusLabels).map(([v,l])=><option key={v} value={v}>{l}</option>)}</select><select value={category} onChange={e=>setCategory(e.target.value)}><option value="ALL">All categories</option>{categories.map(([v,l])=><option key={v} value={v}>{l}</option>)}</select></div>
        {loading&&<State icon="loader" title="Loading your tickets" text="Just a moment while we check the helpdesk."/>}
        {!loading&&error&&<State error title="Unable to load tickets" text={error} action={<button className="secondary" onClick={loadTickets}>Try again</button>}/>} 
        {!loading&&!error&&!filteredTickets.length&&<State title={tickets.length?'No matching tickets':'No tickets yet'} text={tickets.length?'Try changing your search or filters.':'Create your first request and our support team will take it from there.'} action={!tickets.length&&<button className="primary" onClick={()=>setModalOpen(true)}>Create first ticket</button>}/>} 
        {!loading&&!error&&!!filteredTickets.length&&<div className="ticket-list">{filteredTickets.map(ticket=><article className="ticket-card" key={ticket.id}><div className={`category-symbol category-${ticket.category.toLowerCase()}`}>{categoryLabels[ticket.category]?.[0]}</div><div className="ticket-content"><div className="ticket-meta"><span>#{ticket.id}</span><span>•</span><span>{categoryLabels[ticket.category]}</span><span>•</span><span>{formatDate(ticket.createdAt)}</span></div><h3>{ticket.title}</h3><p>{ticket.description}</p><small>Submitted by <strong>{ticket.studentName}</strong></small></div><div className="ticket-actions"><select className={`status-select status-${ticket.status.toLowerCase()}`} value={ticket.status} onChange={e=>updateStatus(ticket,e.target.value)}>{Object.entries(statusLabels).map(([v,l])=><option key={v} value={v}>{l}</option>)}</select><button className="delete-button" onClick={()=>deleteTicket(ticket)} title="Delete"><Icon name="trash" size={18}/></button></div></article>)}</div>}
      </section>
      <section className="support-strip" id="support"><div><span className="eyebrow">Need urgent support?</span><h2>We’re here beyond the portal.</h2><p>Visit Student Services, Monday–Friday, 9:00 AM–5:00 PM.</p></div><a href="mailto:helpdesk@campuscare.edu">helpdesk@campuscare.edu <Icon name="arrow"/></a></section>
    </main>
    <footer><div className="brand footer-brand"><span className="brand-mark"><Icon name="tickets" size={20}/></span><strong>CampusCare</strong></div><span>Making campus life a little easier.</span><span>© 2026 CampusCare</span></footer>
    {modalOpen&&<div className="modal-backdrop" onMouseDown={e=>e.target===e.currentTarget&&setModalOpen(false)}><section className="modal" role="dialog" aria-modal="true"><div className="modal-header"><div><span className="eyebrow">Tell us what happened</span><h2>Create a new ticket</h2></div><button className="icon-button" onClick={()=>setModalOpen(false)}><Icon name="close"/></button></div><form onSubmit={createTicket} noValidate>{formErrors.form&&<div className="form-alert">{formErrors.form}</div>}<Field label="Issue title" error={formErrors.title}><input maxLength="120" value={form.title} onChange={e=>setForm({...form,title:e.target.value})} placeholder="A short summary of the issue"/></Field><Field label="Description" error={formErrors.description}><textarea maxLength="2000" rows="4" value={form.description} onChange={e=>setForm({...form,description:e.target.value})} placeholder="Share details that can help us resolve it..."/></Field><Field label="Category"><select value={form.category} onChange={e=>setForm({...form,category:e.target.value})}>{categories.map(([v,l])=><option key={v} value={v}>{l}</option>)}</select></Field><div className="form-grid"><Field label="Your name" error={formErrors.studentName}><input maxLength="100" value={form.studentName} onChange={e=>setForm({...form,studentName:e.target.value})} placeholder="Asha Rao"/></Field><Field label="Student email" error={formErrors.studentEmail}><input type="email" maxLength="150" value={form.studentEmail} onChange={e=>setForm({...form,studentEmail:e.target.value})} placeholder="you@example.com"/></Field></div><div className="modal-actions"><button type="button" className="secondary" onClick={()=>setModalOpen(false)}>Cancel</button><button className="primary" disabled={submitting}>{submitting?'Creating...':'Submit ticket'}<Icon name="arrow" size={17}/></button></div></form></section></div>}
    {toast&&<div className="toast"><Icon name="check" size={18}/>{toast}</div>}
  </div>
}

function Stat({icon,color,value,label}){return <article><span className={`stat-icon ${color}`}><Icon name={icon}/></span><div><strong>{value}</strong><small>{label}</small></div></article>}
function State({icon,error,title,text,action}){return <div className={`state-card ${error?'error':''}`}>{icon==='loader'?<span className="loader"/>:<span className="empty-icon"><Icon name={error?'alert':'inbox'} size={30}/></span>}<h3>{title}</h3><p>{text}</p>{action}</div>}
function Field({label,error,children}){return <label>{label}{children}{error&&<small className="field-error">{error}</small>}</label>}
export default App
