/**
 * @file admin.js
 * @description Lógica del panel de gestión (admin.html).
 *
 * Solo se puede entrar habiendo iniciado sesión (el usuario se guarda en localStorage
 * desde app.js). Según el rol del usuario se muestran distintas opciones:
 *  - ORGANIZER: solo la pestaña Eventos, y solo los eventos de su lugar asignado.
 *  - ADMIN: todas las pestañas (Eventos, Lugares, Categorías, Usuarios y Reportes).
 *
 * Cada pestaña llama a los endpoints REST del backend con fetch():
 *   /api/events, /api/places, /api/types, /api/users y /api/reports.
 *
 * @author Carmen Viboral
 */

/** URL base de la API REST (backend de Spring Boot). */
const API_URL = 'http://localhost:8080/api';

/** Usuario que ha iniciado sesión (objeto devuelto por /api/users/login). */
let currentUser = null;

/** Lista de lugares cargada de la API; se usa en selectores y tablas. */
let allPlaces = [];

/** Lista de categorías cargada de la API; se usa en selectores y tablas. */
let allTypes = [];

/** Eventos visibles en la tabla (ya filtrados por lugar si el usuario es ORGANIZER). */
let allEvents = [];

/**
 * Al cargar la página:
 *  1. Comprueba que hay un usuario en localStorage; si no, vuelve a index.html.
 *  2. Muestra su nombre y rol en la barra superior.
 *  3. Enseña las pestañas exclusivas de ADMIN si corresponde.
 *  4. Carga los catálogos (lugares y categorías) y los eventos.
 *  5. Si es ADMIN, carga también usuarios y reportes.
 */
document.addEventListener('DOMContentLoaded', async () => {
  const savedUser = localStorage.getItem('carmenUser');
  if (!savedUser) {
    window.location.href = 'index.html';
    return;
  }

  currentUser = JSON.parse(savedUser);
  document.getElementById('userNameDisplay').textContent = currentUser.userName;
  document.getElementById('userRoleBadge').textContent = currentUser.role;

  // Mostrar pestañas exclusivas si es ADMIN
  if (currentUser.role === 'ADMIN') {
    document.querySelectorAll('.admin-only').forEach(el => el.classList.remove('d-none'));
    document.getElementById('panelSubtitle').textContent = 'Control total de eventos, lugares, categorías, usuarios y reportes';
  } else {
    document.getElementById('userRoleBadge').classList.replace('bg-primary', 'bg-success');
    document.getElementById('panelSubtitle').textContent = 'Gestiona los eventos programados en tu escenario asignado';
  }

  await loadCatalogs();
  await loadEvents();

  if (currentUser.role === 'ADMIN') {
    await loadUsers();
    await loadReports();
  }
});

/**
 * Cierra la sesión: borra el usuario de localStorage y vuelve a la cartelera.
 */
function logout() {
  localStorage.removeItem('carmenUser');
  window.location.href = 'index.html';
}

/**
 * Muestra un mensaje en la caja de alertas durante 4 segundos.
 * @param {string} message texto a mostrar
 * @param {'success'|'danger'|'warning'|'info'} [type='success'] color de la alerta (clases de Bootstrap)
 */
function showAlert(message, type = 'success') {
  const box = document.getElementById('alertBox');
  box.className = `alert alert-${type} rounded-3`;
  box.textContent = message;
  box.classList.remove('d-none');
  setTimeout(() => box.classList.add('d-none'), 4000);
}

/**
 * Cambia de pestaña: oculta todas las secciones y muestra solo la elegida.
 * Al abrir Reportes, los vuelve a pedir para que estén actualizados.
 * @param {'events'|'places'|'types'|'users'|'reports'} tabName pestaña a mostrar
 * @param {HTMLButtonElement} btn botón de la pestaña pulsada
 */
function switchTab(tabName, btn) {
  document.querySelectorAll('#adminTabs .nav-link').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  ['events', 'places', 'types', 'users', 'reports'].forEach(sec => {
    document.getElementById(`tab-${sec}`).classList.add('d-none');
  });
  document.getElementById(`tab-${tabName}`).classList.remove('d-none');

  if (tabName === 'reports') loadReports();
}

/**
 * Carga lugares y categorías a la vez (GET /api/places y GET /api/types) y rellena:
 *  - los selectores del formulario de eventos,
 *  - el selector de lugar del formulario de usuarios,
 *  - las tablas de las pestañas Lugares y Categorías.
 * Si el usuario es ORGANIZER, deja fijo su lugar asignado en el selector.
 * @async
 * @returns {Promise<void>}
 */
async function loadCatalogs() {
  const [placesRes, typesRes] = await Promise.all([
    fetch(`${API_URL}/places`),
    fetch(`${API_URL}/types`)
  ]);
  allPlaces = await placesRes.json();
  allTypes = await typesRes.json();

  // Llenar selector de Categorías
  const typeSelect = document.getElementById('eventType');
  typeSelect.innerHTML = allTypes.map(t => `<option value="${t.idTypes}">${t.typeName}</option>`).join('');

  // Llenar selector de Lugares (si es ORGANIZER, fijar su lugar asignado)
  const placeSelect = document.getElementById('eventPlace');
  placeSelect.innerHTML = allPlaces.map(p => `<option value="${p.idPlaces}">${p.placeName}</option>`).join('');

  if (currentUser.role === 'ORGANIZER' && currentUser.idPlace) {
    placeSelect.value = currentUser.idPlace;
    placeSelect.disabled = true;
  }

  // Selector de lugar al crear usuarios (ADMIN)
  const newUserPlace = document.getElementById('newUserPlace');
  if (newUserPlace) {
    newUserPlace.innerHTML = allPlaces.map(p => `<option value="${p.idPlaces}">${p.placeName}</option>`).join('');
  }

  renderPlacesTable();
  renderTypesTable();
}

// ===================== CRUD DE EVENTOS =====================

/**
 * Pide los eventos (GET /api/events) y dibuja la tabla con botones de editar y borrar.
 * Si el usuario es ORGANIZER, solo se muestran los eventos de su lugar.
 * @async
 * @returns {Promise<void>}
 */
async function loadEvents() {
  const res = await fetch(`${API_URL}/events`);
  let events = await res.json();

  // Filtrar si el usuario es ORGANIZER para mostrar solo eventos de su lugar
  if (currentUser.role === 'ORGANIZER' && currentUser.idPlace) {
    events = events.filter(e => e.place && String(e.place.idPlaces) === String(currentUser.idPlace));
  }

  allEvents = events;
  const tbody = document.getElementById('eventsTableBody');

  if (events.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-4">No hay eventos registrados aún.</td></tr>`;
    return;
  }

  tbody.innerHTML = events.map(ev => `
    <tr>
      <td class="fw-bold">${ev.title}<div class="small text-muted fw-normal">${ev.description || ''}</div></td>
      <td><span class="badge-category">${ev.type ? ev.type.typeName : ''}</span></td>
      <td>${ev.place ? ev.place.placeName : ''}</td>
      <td>${ev.eventDate} • ${ev.eventTime.slice(0, 5)}</td>
      <td>${Number(ev.price) === 0 ? '<span class="badge-free">Gratis</span>' : '$' + Number(ev.price).toLocaleString('es-CO')}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-primary rounded-pill me-1" onclick="editEvent(${ev.idEvents})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="deleteEvent(${ev.idEvents})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `).join('');
}

/**
 * Envío del formulario de eventos.
 * Si el campo oculto #eventId tiene valor, es una edición (PUT /api/events/{id});
 * si está vacío, es una creación (POST /api/events).
 * El JSON se arma con la forma que espera la entidad Events de Java:
 * la categoría y el lugar se envían como objetos con solo su ID.
 * Si el backend rechaza el evento (400), se muestra su mensaje de error.
 */
document.getElementById('eventForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const id = document.getElementById('eventId').value;
  // El ORGANIZER siempre crea eventos en su propio lugar
  const selectedPlaceId = currentUser.role === 'ORGANIZER' ? currentUser.idPlace : document.getElementById('eventPlace').value;

  const payload = {
    title: document.getElementById('eventTitle').value,
    description: document.getElementById('eventDescription').value,
    eventDate: document.getElementById('eventDate').value,
    // El input da "HH:mm"; Java (LocalTime) acepta también "HH:mm:ss", así que se añaden los segundos
    eventTime: document.getElementById('eventTime').value.length === 5
      ? document.getElementById('eventTime').value + ':00'
      : document.getElementById('eventTime').value,
    price: parseFloat(document.getElementById('eventPrice').value),
    type: { idTypes: parseInt(document.getElementById('eventType').value) },
    place: { idPlaces: parseInt(selectedPlaceId) }
  };

  const url = id ? `${API_URL}/events/${id}` : `${API_URL}/events`;
  const method = id ? 'PUT' : 'POST';

  const res = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!res.ok) {
    const err = await res.json();
    showAlert(err.message || 'Error al guardar el evento.', 'danger');
    return;
  }

  showAlert(id ? 'Evento actualizado correctamente.' : 'Evento creado correctamente.');
  resetEventForm();
  await loadEvents();
});

/**
 * Pasa el formulario a modo edición: lo rellena con los datos del evento elegido
 * y sube la página hasta el formulario.
 * @param {number} id identificador del evento a editar
 */
function editEvent(id) {
  const ev = allEvents.find(e => e.idEvents === id);
  if (!ev) return;

  document.getElementById('eventId').value = ev.idEvents;
  document.getElementById('eventTitle').value = ev.title;
  document.getElementById('eventDescription').value = ev.description || '';
  document.getElementById('eventDate').value = ev.eventDate;
  document.getElementById('eventTime').value = ev.eventTime.slice(0, 5);
  document.getElementById('eventPrice').value = ev.price;
  if (ev.type) document.getElementById('eventType').value = ev.type.idTypes;
  if (ev.place) document.getElementById('eventPlace').value = ev.place.idPlaces;

  document.getElementById('eventFormTitle').textContent = 'Editar Evento #' + ev.idEvents;
  document.getElementById('cancelEditEventBtn').classList.remove('d-none');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

/**
 * Limpia el formulario de eventos y lo devuelve al modo "crear".
 */
function resetEventForm() {
  document.getElementById('eventForm').reset();
  document.getElementById('eventId').value = '';
  document.getElementById('eventFormTitle').textContent = 'Crear Nuevo Evento';
  document.getElementById('cancelEditEventBtn').classList.add('d-none');
  if (currentUser.role === 'ORGANIZER' && currentUser.idPlace) {
    document.getElementById('eventPlace').value = currentUser.idPlace;
  }
}

/**
 * Pide confirmación y borra un evento (DELETE /api/events/{id}).
 * @async
 * @param {number} id identificador del evento
 */
async function deleteEvent(id) {
  if (!confirm('¿Seguro que deseas eliminar este evento?')) return;
  await fetch(`${API_URL}/events/${id}`, { method: 'DELETE' });
  showAlert('Evento eliminado.');
  await loadEvents();
}

// ===================== CRUD DE LUGARES (solo ADMIN) =====================

/**
 * Dibuja la tabla de lugares a partir de {@link allPlaces}.
 */
function renderPlacesTable() {
  const tbody = document.getElementById('placesTableBody');
  if (!tbody) return;
  tbody.innerHTML = allPlaces.map(p => `
    <tr>
      <td>${p.idPlaces}</td>
      <td class="fw-bold">${p.placeName}</td>
      <td>${p.adress}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="deletePlace(${p.idPlaces})"><i class="bi bi-trash"></i></button>
      </td>
    </tr>
  `).join('');
}

/**
 * Envío del formulario de lugares: crea un lugar (POST /api/places)
 * y recarga los catálogos para que aparezca en tablas y selectores.
 */
document.getElementById('placeForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  await fetch(`${API_URL}/places`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      placeName: document.getElementById('placeName').value,
      adress: document.getElementById('placeAdress').value
    })
  });
  document.getElementById('placeForm').reset();
  showAlert('Lugar cultural agregado.');
  await loadCatalogs();
});

/**
 * Pide confirmación y borra un lugar (DELETE /api/places/{id}).
 * @async
 * @param {number} id identificador del lugar
 */
async function deletePlace(id) {
  if (!confirm('¿Eliminar este lugar?')) return;
  await fetch(`${API_URL}/places/${id}`, { method: 'DELETE' });
  showAlert('Lugar eliminado.');
  await loadCatalogs();
}

// ===================== CRUD DE CATEGORÍAS (solo ADMIN) =====================

/**
 * Dibuja la tabla de categorías a partir de {@link allTypes}.
 */
function renderTypesTable() {
  const tbody = document.getElementById('typesTableBody');
  if (!tbody) return;
  tbody.innerHTML = allTypes.map(t => `
    <tr>
      <td>${t.idTypes}</td>
      <td class="fw-bold">${t.typeName}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="deleteType(${t.idTypes})"><i class="bi bi-trash"></i></button>
      </td>
    </tr>
  `).join('');
}

/**
 * Envío del formulario de categorías: crea una categoría (POST /api/types)
 * y recarga los catálogos.
 */
document.getElementById('typeForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  await fetch(`${API_URL}/types`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ typeName: document.getElementById('typeName').value })
  });
  document.getElementById('typeForm').reset();
  showAlert('Categoría agregada.');
  await loadCatalogs();
});

/**
 * Pide confirmación y borra una categoría (DELETE /api/types/{id}).
 * @async
 * @param {number} id identificador de la categoría
 */
async function deleteType(id) {
  if (!confirm('¿Eliminar esta categoría?')) return;
  await fetch(`${API_URL}/types/${id}`, { method: 'DELETE' });
  showAlert('Categoría eliminada.');
  await loadCatalogs();
}

// ===================== CRUD DE USUARIOS (solo ADMIN) =====================

/**
 * Oculta el selector de lugar cuando el rol elegido es ADMIN
 * (un administrador no está ligado a un lugar concreto).
 */
function toggleUserPlaceField() {
  const role = document.getElementById('newUserRole').value;
  document.getElementById('userPlaceWrapper').classList.toggle('d-none', role === 'ADMIN');
}

/**
 * Pide los usuarios (GET /api/users) y dibuja su tabla.
 * @async
 * @returns {Promise<void>}
 */
async function loadUsers() {
  const res = await fetch(`${API_URL}/users`);
  const users = await res.json();
  const tbody = document.getElementById('usersTableBody');
  tbody.innerHTML = users.map(u => `
    <tr>
      <td class="fw-bold">${u.userName}</td>
      <td>${u.email}</td>
      <td><span class="badge ${u.role === 'ADMIN' ? 'bg-primary' : 'bg-success'}">${u.role}</span></td>
      <td>${u.role === 'ORGANIZER' ? (u.idPlace || 'Sin asignar') : 'Todos'}</td>
      <td class="text-end">
        <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="deleteUser(${u.idUser})"><i class="bi bi-trash"></i></button>
      </td>
    </tr>
  `).join('');
}

/**
 * Envío del formulario de usuarios: crea un usuario (POST /api/users).
 * Solo se envía idPlace si el rol es ORGANIZER.
 * Si el correo ya existe, el backend responde 400 y se muestra su mensaje.
 */
document.getElementById('userForm')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const role = document.getElementById('newUserRole').value;
  const res = await fetch(`${API_URL}/users`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userName: document.getElementById('newUserName').value,
      email: document.getElementById('newUserEmail').value,
      role,
      idPlace: role === 'ORGANIZER' ? document.getElementById('newUserPlace').value : null,
      password: document.getElementById('newUserPassword').value
    })
  });

  if (!res.ok) {
    const err = await res.json();
    showAlert(err.message || 'Error al crear usuario.', 'danger');
    return;
  }

  document.getElementById('userForm').reset();
  toggleUserPlaceField();
  showAlert('Usuario creado correctamente.');
  await loadUsers();
});

/**
 * Pide confirmación y borra un usuario (DELETE /api/users/{id}).
 * @async
 * @param {number} id identificador del usuario
 */
async function deleteUser(id) {
  if (!confirm('¿Eliminar este usuario?')) return;
  await fetch(`${API_URL}/users/${id}`, { method: 'DELETE' });
  showAlert('Usuario eliminado.');
  await loadUsers();
}

// ===================== REPORTES (solo ADMIN) =====================

/**
 * Pide el informe (GET /api/reports) y rellena las tres cifras
 * (total, gratis, de pago) y las dos listas (por categoría y por lugar).
 * @async
 * @returns {Promise<void>}
 */
async function loadReports() {
  const res = await fetch(`${API_URL}/reports`);
  const data = await res.json();

  document.getElementById('repTotal').textContent = data.totalEvents;
  document.getElementById('repFree').textContent = data.freeEvents;
  document.getElementById('repPaid').textContent = data.paidEvents;

  // eventsByType y eventsByPlace llegan como objetos { "nombre": cantidad }
  const byTypeList = document.getElementById('repByType');
  byTypeList.innerHTML = Object.entries(data.eventsByType || {})
    .map(([k, v]) => `<li class="list-group-item d-flex justify-content-between"><span>${k}</span><strong>${v}</strong></li>`)
    .join('');

  const byPlaceList = document.getElementById('repByPlace');
  byPlaceList.innerHTML = Object.entries(data.eventsByPlace || {})
    .map(([k, v]) => `<li class="list-group-item d-flex justify-content-between"><span>${k}</span><strong>${v}</strong></li>`)
    .join('');
}
