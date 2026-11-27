/**
 * @file app.js
 * @description Lógica de la página pública (index.html): la "cartelera" de eventos.
 *
 * Qué hace:
 *  1. Pide todos los eventos a la API de Spring Boot (GET /api/events).
 *  2. Los muestra como tarjetas, aplicando filtros por fecha (hoy, semana, mes)
 *     y por precio (gratis / de pago). Los filtros se aplican aquí, en el navegador.
 *  3. Gestiona el formulario de inicio de sesión (POST /api/users/login). Si el
 *     login es correcto, guarda el usuario en localStorage y redirige a admin.html.
 *
 * @author Carmen Viboral
 */

/** URL base de la API REST (el backend de Spring Boot corriendo en local). */
const API_URL = 'http://localhost:8080/api';

/** Copia de todos los eventos recibidos de la API. Se filtra sin volver a pedirlos. */
let allEvents = [];

/** Filtro de fecha activo: 'ALL' (todos), 'DAY' (hoy), 'WEEK' (próximos 7 días) o 'MONTH' (mes actual). */
let currentTimeFilter = 'ALL';

/** Filtro de precio activo: 'ALL' (todos), 'FREE' (gratis) o 'PAID' (de pago). */
let currentPriceFilter = 'ALL';

// 1. Cargar eventos al abrir la página
document.addEventListener('DOMContentLoaded', loadEvents);

/**
 * Pide la lista de eventos a la API y la dibuja.
 * Si el servidor no responde, muestra un mensaje de error en la página.
 * @async
 * @returns {Promise<void>}
 */
async function loadEvents() {
  try {
    const response = await fetch(`${API_URL}/events`);
    allEvents = await response.json();
    renderEvents();
  } catch (error) {
    document.getElementById('eventsContainer').innerHTML = `
      <div class="col-12 text-center py-5">
        <div class="alert alert-danger d-inline-block">
          No se pudo conectar con el servidor Spring Boot en el puerto 8080.
        </div>
      </div>`;
  }
}

/**
 * Cambia el filtro de fecha, marca el botón pulsado como activo y vuelve a dibujar.
 * @param {'ALL'|'DAY'|'WEEK'|'MONTH'} filter filtro elegido
 * @param {HTMLButtonElement} btn botón que se pulsó
 */
function setTimeFilter(filter, btn) {
  currentTimeFilter = filter;
  btn.parentElement.querySelectorAll('button').forEach(b => {
    b.classList.remove('btn-primary', 'active');
    b.classList.add('btn-outline-primary');
  });
  btn.classList.remove('btn-outline-primary');
  btn.classList.add('btn-primary', 'active');
  renderEvents();
}

/**
 * Cambia el filtro de precio, restaura el estilo de los demás botones y vuelve a dibujar.
 * @param {'ALL'|'FREE'|'PAID'} filter filtro elegido
 * @param {HTMLButtonElement} btn botón que se pulsó
 */
function setPriceFilter(filter, btn) {
  currentPriceFilter = filter;
  btn.parentElement.querySelectorAll('button').forEach(b => {
    b.classList.remove('btn-dark', 'btn-success', 'btn-warning', 'active');
    if (b.textContent.includes('Gratis') && !b.textContent.includes('No')) b.classList.add('btn-outline-success');
    else if (b.textContent.includes('No Gratis')) b.classList.add('btn-outline-warning');
    else b.classList.add('btn-outline-dark');
  });
  btn.classList.remove('btn-outline-dark', 'btn-outline-success', 'btn-outline-warning');
  btn.classList.add('btn-dark', 'active');
  renderEvents();
}

/**
 * Aplica los filtros activos sobre {@link allEvents}, ordena el resultado por fecha
 * y hora, y genera el HTML de las tarjetas dentro de #eventsContainer.
 * Si ningún evento cumple los filtros, muestra un mensaje vacío.
 */
function renderEvents() {
  // Fecha de hoy a las 00:00, para comparar solo días (sin horas)
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const filtered = allEvents.filter(ev => {
    // Filtro por precio
    const price = parseFloat(ev.price);
    if (currentPriceFilter === 'FREE' && price > 0) return false;
    if (currentPriceFilter === 'PAID' && price === 0) return false;

    // Se construye la fecha a mano ("2026-12-01" → año, mes, día) para evitar
    // desfases de zona horaria que ocurren con new Date("2026-12-01")
    const [year, month, day] = ev.eventDate.split('-').map(Number);
    const evDate = new Date(year, month - 1, day);

    // Filtro por fecha
    if (currentTimeFilter === 'DAY') {
      return evDate.getTime() === today.getTime();
    }
    if (currentTimeFilter === 'WEEK') {
      const nextWeek = new Date(today);
      nextWeek.setDate(today.getDate() + 7);
      return evDate >= today && evDate <= nextWeek;
    }
    if (currentTimeFilter === 'MONTH') {
      return evDate.getMonth() === today.getMonth() && evDate.getFullYear() === today.getFullYear();
    }
    return true;
  });

  // Orden cronológico: primero por fecha y, si coincide, por hora
  filtered.sort((a, b) => a.eventDate.localeCompare(b.eventDate) || a.eventTime.localeCompare(b.eventTime));

  const container = document.getElementById('eventsContainer');
  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5">
        <i class="bi bi-calendar-x fs-1 text-muted"></i>
        <h5 class="mt-3 fw-bold">No hay eventos para este filtro</h5>
        <p class="text-muted">Prueba seleccionando "Todos" para ver toda la programación cultural.</p>
      </div>`;
    return;
  }

  // Una tarjeta por evento
  container.innerHTML = filtered.map(ev => {
    const isFree = parseFloat(ev.price) === 0;
    const priceBadge = isFree
      ? `<span class="badge-free"><i class="bi bi-check-circle me-1"></i>Gratis</span>`
      : `<span class="badge-paid">$${Number(ev.price).toLocaleString('es-CO')}</span>`;

    return `
      <div class="col-md-6 col-lg-4">
        <div class="event-card p-4 d-flex flex-column justify-content-between">
          <div>
            <div class="d-flex justify-content-between align-items-center mb-3">
              <span class="badge-category">${ev.type ? ev.type.typeName : 'Cultural'}</span>
              ${priceBadge}
            </div>
            <h5 class="fw-bold mb-2">${ev.title}</h5>
            <p class="text-muted small mb-3">${ev.description || 'Evento cultural en El Carmen de Viboral.'}</p>
          </div>
          <div class="border-top pt-3 small text-secondary">
            <div class="mb-1"><i class="bi bi-calendar-event text-primary me-2"></i><strong>Fecha:</strong> ${ev.eventDate} • ${ev.eventTime.slice(0, 5)}</div>
            <div><i class="bi bi-geo-alt-fill text-danger me-2"></i><strong>Lugar:</strong> ${ev.place ? ev.place.placeName : ''} (${ev.place ? ev.place.adress : ''})</div>
          </div>
        </div>
      </div>`;
  }).join('');
}

/**
 * Envío del formulario de login.
 * Manda el correo y la contraseña a POST /api/users/login.
 *  - Si la respuesta es correcta (200), guarda el usuario devuelto en localStorage
 *    con la clave "carmenUser" y redirige al panel (admin.html).
 *  - Si no (401), muestra el mensaje de error que envía el backend.
 */
document.getElementById('loginForm').addEventListener('submit', async (e) => {
  e.preventDefault(); // evita que el formulario recargue la página
  const email = document.getElementById('loginEmail').value;
  const password = document.getElementById('loginPassword').value;
  const errorBox = document.getElementById('loginError');

  try {
    const res = await fetch(`${API_URL}/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    const data = await res.json();
    if (!res.ok) {
      errorBox.textContent = data.message || 'Credenciales incorrectas.';
      errorBox.classList.remove('d-none');
      return;
    }

    localStorage.setItem('carmenUser', JSON.stringify(data));
    window.location.href = 'admin.html';
  } catch (err) {
    errorBox.textContent = 'Error de conexión con el servidor.';
    errorBox.classList.remove('d-none');
  }
});
