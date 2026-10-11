'use strict';
const $ = (id) => document.getElementById(id);
const money = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' });
const config = {
  productos: { title: 'Productos', singular: 'producto', subtitle: 'Cada producto en orden. Cada detalle bajo control.', columns: ['ID', 'Producto', 'Precio', 'Stock', 'Acciones'] },
  categorias: { title: 'Categorías', singular: 'categoría', subtitle: 'Dale un lugar a cada idea. Organiza tus categorías.', columns: ['ID', 'Categoría', 'Descripción', 'Acciones'] }
};
let moduleName = 'productos';
let records = { productos: [], categorias: [] };
let editingId = null;
let deletingRecord = null;
let busy = false;
let loaded = false;

async function api(path, options = {}) {
  const response = await fetch('/api/' + path, options);
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.errores ? Object.values(body.errores).join('. ') : body.mensaje || 'No se pudo completar la operación. Intenta nuevamente.');
  }
  return response.status === 204 ? null : response.json();
}
function notify(text, error = false) {
  $('message').textContent = text;
  $('message').classList.toggle('error', error);
  $('message').hidden = false;
}
async function load() {
  $('refresh').disabled = true;
  try {
    const [productos, categorias] = await Promise.all([api('productos'), api('categorias')]);
    records = { productos, categorias };
    loaded = true;
    $('product-count').textContent = productos.length;
    $('category-count').textContent = categorias.length;
    $('inventory-value').textContent = money.format(productos.reduce((sum, p) => sum + p.precio * p.stock, 0));
    render();
    return true;
  } catch (error) {
    notify('No pudimos actualizar los datos. Comprueba que la aplicación esté encendida. ' + error.message, true);
    $('result-count').textContent = loaded ? 'Datos anteriores · actualización pendiente' : 'No se pudieron cargar los datos';
    return false;
  } finally { $('refresh').disabled = false; }
}
function cell(text, className = '') {
  const element = document.createElement('td');
  element.textContent = text;
  element.className = className;
  return element;
}
function render() {
  const cfg = config[moduleName];
  $('title').textContent = $('breadcrumb').textContent = cfg.title;
  $('subtitle').textContent = cfg.subtitle;
  $('new-button').textContent = '+ ' + (moduleName === 'productos' ? 'Nuevo producto' : 'Nueva categoría');
  $('list-title').textContent = (moduleName === 'productos' ? 'Todos los productos' : 'Todas las categorías');
  $('list-description').textContent = moduleName === 'productos' ? 'Consulta y administra tu inventario.' : 'Consulta y administra tus categorías.';
  document.querySelectorAll('[data-module]').forEach(button => {
    const active = button.dataset.module === moduleName;
    button.classList.toggle('active', active);
    button.setAttribute('aria-pressed', active);
  });
  const head = document.createElement('tr');
  cfg.columns.forEach(title => { const th = document.createElement('th'); th.scope = 'col'; th.textContent = title; head.append(th); });
  $('table-head').replaceChildren(head);
  const query = $('search').value.trim().toLocaleLowerCase('es');
  const list = records[moduleName].filter(record => record.nombre.toLocaleLowerCase('es').includes(query));
  $('table-body').replaceChildren();
  for (const record of list) {
    const row = document.createElement('tr');
    row.append(cell('#' + String(record.id).padStart(3, '0'), 'id-cell'));
    const name = cell('', 'name-cell');
    const nameWrap = document.createElement('div'); nameWrap.className = 'record-name';
    const icon = document.createElement('span'); icon.className = 'record-icon'; icon.textContent = moduleName === 'productos' ? '▦' : '▤'; icon.setAttribute('aria-hidden', 'true');
    const label = document.createElement('span'); label.textContent = record.nombre;
    nameWrap.append(icon, label); name.append(nameWrap); row.append(name);
    if (moduleName === 'productos') {
      row.append(cell(money.format(record.precio)));
      const stock = cell(''); const badge = document.createElement('span'); badge.className = 'badge' + (record.stock === 0 ? ' zero' : ''); badge.textContent = record.stock === 0 ? 'Sin stock' : record.stock + ' unidades'; stock.append(badge); row.append(stock);
    } else { row.append(cell(record.descripcion, 'description-cell')); }
    const actions = cell(''); const wrap = document.createElement('div'); wrap.className = 'row-actions';
    const edit = document.createElement('button'); edit.textContent = 'Editar'; edit.setAttribute('aria-label', 'Editar ' + record.nombre); edit.addEventListener('click', () => openEditor(record));
    const remove = document.createElement('button'); remove.textContent = 'Eliminar'; remove.className = 'delete'; remove.setAttribute('aria-label', 'Eliminar ' + record.nombre); remove.addEventListener('click', () => openDelete(record));
    wrap.append(edit, remove); actions.append(wrap); row.append(actions); $('table-body').append(row);
  }
  $('empty').hidden = list.length > 0;
  $('empty-title').textContent = query ? 'No encontramos coincidencias' : moduleName === 'productos' ? 'Tu inventario empieza aquí' : 'Un lugar para cada categoría';
  $('empty-description').textContent = query ? 'Prueba con otro nombre para encontrar lo que buscas.' : 'Agrega tu primer registro para empezar a organizarlo todo.';
  $('empty-create').hidden = Boolean(query);
  $('empty-create').textContent = 'Agregar ' + cfg.singular;
  $('result-count').textContent = list.length + ' de ' + records[moduleName].length + ' ' + cfg.title.toLocaleLowerCase('es');
}
function openEditor(record = null) {
  const product = moduleName === 'productos';
  editingId = record?.id ?? null;
  $('editor-form').reset();
  $('form-error').hidden = true;
  $('editor-title').textContent = record ? 'Editar ' + config[moduleName].singular : product ? 'Nuevo producto' : 'Nueva categoría';
  $('save-button').textContent = 'Guardar ' + config[moduleName].singular;
  $('product-fields').hidden = !product;
  $('description-field').hidden = product;
  $('precio').disabled = $('stock').disabled = !product;
  $('descripcion').disabled = product;
  $('descripcion').required = !product;
  if (record) {
    $('nombre').value = record.nombre;
    if (product) { $('precio').value = record.precio; $('stock').value = record.stock; }
    else { $('descripcion').value = record.descripcion; }
  }
  $('editor').showModal();
  $('nombre').focus();
}
function closeEditor() { if (!busy) $('editor').close(); }
$('new-button').addEventListener('click', () => openEditor());
$('empty-create').addEventListener('click', () => openEditor());
$('close-editor').addEventListener('click', closeEditor);
$('cancel-editor').addEventListener('click', closeEditor);
$('search').addEventListener('input', render);
$('refresh').addEventListener('click', async () => { $('message').hidden = true; await load(); });
document.querySelectorAll('[data-module]').forEach(button => button.addEventListener('click', () => { moduleName = button.dataset.module; $('search').value = ''; $('message').hidden = true; render(); }));
$('editor').addEventListener('cancel', event => { if (busy) event.preventDefault(); });
$('delete-dialog').addEventListener('cancel', event => { if (busy) event.preventDefault(); });
function setBusy(value) {
  busy = value;
  ['save-button', 'cancel-editor', 'close-editor', 'confirm-delete', 'cancel-delete'].forEach(id => { $(id).disabled = value; });
}
$('editor-form').addEventListener('submit', async event => {
  event.preventDefault();
  if (busy) return;
  const nombre = $('nombre').value.trim();
  const payload = moduleName === 'productos' ? { nombre, precio: Number($('precio').value), stock: Number($('stock').value) } : { nombre, descripcion: $('descripcion').value.trim() };
  if (!nombre || (moduleName === 'categorias' && !payload.descripcion)) {
    $('form-error').textContent = 'Completa los campos obligatorios; no pueden contener solo espacios.'; $('form-error').hidden = false; return;
  }
  setBusy(true); $('form-error').hidden = true;
  try {
    await api(moduleName + (editingId === null ? '' : '/' + editingId), { method: editingId === null ? 'POST' : 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    $('editor').close();
    notify('Registro ' + (editingId === null ? 'creado' : 'actualizado') + ' correctamente.');
    await load();
  } catch (error) { $('form-error').textContent = error.message; $('form-error').hidden = false; }
  finally { setBusy(false); }
});
function openDelete(record) {
  deletingRecord = record;
  $('delete-description').textContent = 'Se eliminará «' + record.nombre + '» de tus ' + config[moduleName].title.toLocaleLowerCase('es') + '.';
  $('delete-error').hidden = true;
  $('delete-dialog').showModal();
  $('cancel-delete').focus();
}
$('cancel-delete').addEventListener('click', () => { if (!busy) $('delete-dialog').close(); });
$('confirm-delete').addEventListener('click', async () => {
  if (busy || !deletingRecord) return;
  setBusy(true); $('delete-error').hidden = true;
  try {
    await api(moduleName + '/' + deletingRecord.id, { method: 'DELETE' });
    $('delete-dialog').close(); notify('Registro eliminado correctamente.'); await load();
  } catch (error) { $('delete-error').textContent = error.message; $('delete-error').hidden = false; }
  finally { setBusy(false); }
});
load();
