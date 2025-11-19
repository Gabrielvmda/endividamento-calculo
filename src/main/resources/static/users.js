(function(){
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {

    const base = '/api';

    const tb = document.querySelector('#usersTable tbody');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const passInput = document.getElementById('passwordHash');
    const saveBtn = document.getElementById('saveBtn');
    const resetBtn = document.getElementById('resetBtn');
    const refreshBtn = document.getElementById('refreshBtn');
    const summaryUserId = document.getElementById('summaryUserId');
    const getSummaryBtn = document.getElementById('getSummaryBtn');
    const summaryResult = document.getElementById('summaryResult');

    const incomesContainer = document.getElementById('incomesContainer');
    const expensesContainer = document.getElementById('expensesContainer');
    const debtsContainer = document.getElementById('debtsContainer');
    const addIncomeBtn = document.getElementById('addIncome');
    const addExpenseBtn = document.getElementById('addExpense');
    const addDebtBtn = document.getElementById('addDebt');

    let editingId = null;
    let incomes = [];
    let expenses = [];
    let debts = [];


    async function fetchJson(url, opts) {
      const r = await fetch(url, Object.assign({headers:{'Content-Type':'application/json'}}, opts));
      if (!r.ok) {
        const text = await r.text().catch(() => '');
        throw new Error(`${r.status} ${r.statusText}: ${text}`);
      }
      return r.status === 204 ? null : r.json();
    }

    function parseNumber(v) {
      if (!v) return null;
      const n = Number(v.toString().replace(',', '.'));
      return isNaN(n) ? null : n;
    }


    async function loadUsers() {
      const users = await fetchJson(`${base}/users`);
      tb.innerHTML = '';
      users.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>${u.id}</td>
          <td>${u.name}</td>
          <td>${u.email}</td>
          <td>${u.createdAt ? new Date(u.createdAt).toLocaleString() : ''}</td>
          <td>
            <button class="edit" data-id="${u.id}">Editar</button>
            <button class="del" data-id="${u.id}">Excluir</button>
            <button class="summary" data-id="${u.id}">Summary</button>
          </td>`;
        tb.appendChild(tr);
      });
    }

    const createUser = (p) => fetchJson(`${base}/users`, { method:'POST', body:JSON.stringify(p) });
    const updateUser = (id,p) => fetchJson(`${base}/users/${id}`, { method:'PUT', body:JSON.stringify(p) });
    const deleteUser = (id) => fetchJson(`${base}/users/${id}`, { method:'DELETE' });

    const createIncome = (p) => fetchJson(`${base}/incomes`, { method:'POST', body:JSON.stringify(p) });
    const updateIncome = (id,p) => fetchJson(`${base}/incomes/${id}`, { method:'PUT', body:JSON.stringify(p) });
    const deleteIncome = (id) => fetchJson(`${base}/incomes/${id}`, { method:'DELETE' });

    const createExpense = (p) => fetchJson(`${base}/expenses`, { method:'POST', body:JSON.stringify(p) });
    const updateExpense = (id,p) => fetchJson(`${base}/expenses/${id}`, { method:'PUT', body:JSON.stringify(p) });
    const deleteExpense = (id) => fetchJson(`${base}/expenses/${id}`, { method:'DELETE' });

    const createDebt = (p) => fetchJson(`${base}/debts`, { method:'POST', body:JSON.stringify(p) });
    const updateDebt = (id,p) => fetchJson(`${base}/debts/${id}`, { method:'PUT', body:JSON.stringify(p) });
    const deleteDebt = (id) => fetchJson(`${base}/debts/${id}`, { method:'DELETE' });

    async function fetchSummary(id) {
      const data = await fetchJson(`${base}/summary/user/${id}`);
      summaryResult.innerHTML = `<pre>${JSON.stringify(data,null,2)}</pre>`;
    }


    function renderIncomes() {
      incomesContainer.innerHTML = '';
      incomes.forEach((inc, idx) => {
        incomesContainer.innerHTML += `
          <div class="list-grid">
            <input class="inc-amount" data-idx="${idx}" placeholder="monthlyAmount" value="${inc.monthlyAmount ?? ''}">
            <input class="inc-source" data-idx="${idx}" placeholder="source" value="${inc.source ?? ''}">
            <div></div>
            <button class="mini remove-inc" data-idx="${idx}">Remover</button>
          </div>`;
      });
    }

    function renderExpenses() {
      expensesContainer.innerHTML = '';
      expenses.forEach((exp, idx) => {
        expensesContainer.innerHTML += `
          <div class="list-grid">
            <input class="exp-desc" data-idx="${idx}" placeholder="description" value="${exp.description ?? ''}">
            <input class="exp-amount" data-idx="${idx}" placeholder="monthlyAmount" value="${exp.monthlyAmount ?? ''}">
            <div></div>
            <button class="mini remove-exp" data-idx="${idx}">Remover</button>
          </div>`;
      });
    }

    function renderDebts() {
      debtsContainer.innerHTML = '';
      debts.forEach((d, idx) => {
        debtsContainer.innerHTML += `
          <div class="list-grid">
            <input class="debt-creditor" data-idx="${idx}" placeholder="creditor" value="${d.creditor ?? ''}">
            <input class="debt-total" data-idx="${idx}" placeholder="totalAmount" value="${d.totalAmount ?? ''}">
            <input class="debt-rate" data-idx="${idx}" placeholder="annualInterestRate" value="${d.annualInterestRate ?? ''}">
            <div>
              <input class="debt-min" data-idx="${idx}" placeholder="minimumInstallment" value="${d.minimumInstallment ?? ''}">
              <button class="mini remove-debt" data-idx="${idx}">Remover</button>
            </div>
          </div>`;
      });
    }


    addIncomeBtn.onclick = () => { incomes.push({}); renderIncomes(); };
    addExpenseBtn.onclick = () => { expenses.push({}); renderExpenses(); };
    addDebtBtn.onclick = () => { debts.push({}); renderDebts(); };

    // Remover usuarios
    document.body.addEventListener('click', async (ev) => {
      const t = ev.target;


      if (t.classList.contains('remove-inc')) {
        const i = +t.dataset.idx;
        if (incomes[i].id) await deleteIncome(incomes[i].id);
        incomes.splice(i,1);
        return renderIncomes();
      }


      if (t.classList.contains('remove-exp')) {
        const i = +t.dataset.idx;
        if (expenses[i].id) await deleteExpense(expenses[i].id);
        expenses.splice(i,1);
        return renderExpenses();
      }


      if (t.classList.contains('remove-debt')) {
        const i = +t.dataset.idx;
        if (debts[i].id) await deleteDebt(debts[i].id);
        debts.splice(i,1);
        return renderDebts();
      }


      if (t.classList.contains('edit')) {
        const id = t.dataset.id;
        editingId = id;

        const u = await fetchJson(`${base}/users/${id}`);
        nameInput.value = u.name;
        emailInput.value = u.email;
        passInput.value = u.passwordHash;

        incomes = await fetchJson(`${base}/incomes/user/${id}`).catch(() => []);
        expenses = await fetchJson(`${base}/expenses/user/${id}`).catch(() => []);
        debts = await fetchJson(`${base}/debts/user/${id}`).catch(() => []);

        renderIncomes();
        renderExpenses();
        renderDebts();

        window.scrollTo({ top: 0, behavior:'smooth' });
      }

      if (t.classList.contains('del')) {
        const id = t.dataset.id;
        if (!confirm('Confirmar exclusão?')) return;
        await deleteUser(id);
        return loadUsers();
      }

      if (t.classList.contains('summary')) {
        const id = t.dataset.id;
        return fetchSummary(id);
      }
    });


    document.body.addEventListener('input', (ev) => {
      const t = ev.target;
      const idx = t.dataset.idx;
      if (idx === undefined) return;

      const i = +idx;

      if (t.classList.contains('inc-amount')) incomes[i].monthlyAmount = parseNumber(t.value);
      if (t.classList.contains('inc-source')) incomes[i].source = t.value;

      if (t.classList.contains('exp-desc')) expenses[i].description = t.value;
      if (t.classList.contains('exp-amount')) expenses[i].monthlyAmount = parseNumber(t.value);

      if (t.classList.contains('debt-creditor')) debts[i].creditor = t.value;
      if (t.classList.contains('debt-total')) debts[i].totalAmount = parseNumber(t.value);
      if (t.classList.contains('debt-rate')) debts[i].annualInterestRate = parseNumber(t.value);
      if (t.classList.contains('debt-min')) debts[i].minimumInstallment = parseNumber(t.value);
    });

    // Salvar criação
    saveBtn.onclick = async () => {
      const payload = {
        name: nameInput.value.trim(),
        email: emailInput.value.trim(),
        passwordHash: passInput.value.trim()
      };

      if (!payload.name || !payload.email){
        alert("Nome e email obrigatórios");
        return;
      }

      // Edição de usuario
      if (editingId) {

        await updateUser(editingId, Object.assign({ id: editingId }, payload));


        for (const inc of incomes){
          if (inc.id){
            await updateIncome(inc.id, {...inc, user:{id: editingId}});
          } else {
            if (!inc.source && !inc.monthlyAmount) continue;
            await createIncome({...inc, user:{id: editingId}});
          }
        }


        for (const exp of expenses){
          if (exp.id){
            await updateExpense(exp.id, {...exp, user:{id: editingId}});
          } else {
            if (!exp.description && !exp.monthlyAmount) continue;
            await createExpense({...exp, user:{id: editingId}});
          }
        }


        for (const d of debts){
          if (d.id){
            await updateDebt(d.id, {...d, user:{id: editingId}});
          } else {
            if (!d.creditor && !d.totalAmount) continue;
            await createDebt({...d, user:{id: editingId}});
          }
        }

      } else {

       //Criação de novo user
        const saved = await createUser(payload);
        const userId = saved.id;

        for (const inc of incomes){
          if (!inc.source && !inc.monthlyAmount) continue;
          await createIncome({...inc, user:{id: userId}});
        }

        for (const exp of expenses){
          if (!exp.description && !exp.monthlyAmount) continue;
          await createExpense({...exp, user:{id: userId}});
        }

        for (const d of debts){
          if (!d.creditor && !d.totalAmount) continue;
          await createDebt({...d, user:{id: userId}});
        }
      }

      resetForm();
      await loadUsers();
    };

    function resetForm(){
      editingId = null;
      nameInput.value = '';
      emailInput.value = '';
      passInput.value = '';
      incomes = [];
      expenses = [];
      debts = [];
      renderIncomes();
      renderExpenses();
      renderDebts();
    }


    if (getSummaryBtn && summaryUserId) {
      getSummaryBtn.addEventListener('click', () => {
        const id = summaryUserId.value.trim();
        if (!id) { alert('Informe user id'); return; }
        fetchSummary(id).catch(e => {
          if (summaryResult) summaryResult.innerHTML = `<div class="small">Erro: ${e.message}</div>`;
          console.error('fetchSummary error', e);
        });
      });


      summaryUserId.addEventListener('keydown', (ev) => {
        if (ev.key === 'Enter') {
          ev.preventDefault();
          getSummaryBtn.click();
        }
      });
    } else {
      console.warn('users.js: getSummaryBtn ou summaryUserId não encontrado');
    }

    // === INIT ===
    loadUsers();
    renderIncomes();
    renderExpenses();
    renderDebts();

  });
})();
