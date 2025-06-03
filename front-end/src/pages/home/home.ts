import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Menu } from '../../components/menu/menu';
import { TransactionModalComponent } from '../../components/transaction-modal/transaction-modal.component';
import { TransactionService } from '../../components/transaction.service';
import { AuthService } from '../../app/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, Menu, TransactionModalComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  saldo: number = 0;
  transacoes: { data: string; descricao: string; valor: number }[] = [];
  carregandoSaldo: boolean = true;
  carregandoGrafico: boolean = true;
  carregandoTransacoes: boolean = true;

  // Dados para o gráfico de gastos
  dadosGrafico: { categoria: string; valor: number }[] = [];

  // Dados para cartões de resumo
  cartoes: { titulo: string; valor: number; icone: string }[] = [];

  // Metas financeiras
  metas: { descricao: string; valorAtual: number; valorMeta: number; percentual: number }[] = [];

  constructor(
    private transactionService: TransactionService,
    private authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  // Modal de transação
  showTransactionModal: boolean = false;
  users: any[] = [];
  accounts: any[] = [];

  ngOnInit() {
    this.carregarDados();
    this.carregarUsuariosEContas();
  }

  carregarUsuariosEContas() {
    // Dados de exemplo para usuários
    this.users = [
      { id: '1', name: 'João Silva' },
      { id: '2', name: 'Maria Oliveira' },
      { id: '3', name: 'Pedro Santos' },
    ];

    // Dados de exemplo para contas
    this.accounts = [
      { id: '1', userId: '1', name: 'Conta Corrente', number: '12345-6' },
      { id: '2', userId: '1', name: 'Conta Poupança', number: '65432-1' },
      { id: '3', userId: '2', name: 'Conta Corrente', number: '98765-4' },
      { id: '4', userId: '3', name: 'Conta Investimento', number: '45678-9' },
    ];
  }

  carregarDados() {
    // Dados de saldo
    this.saldo = 3547.82;

    // Dados de transações
    this.transacoes = [
      { data: '2023-11-28', descricao: 'Supermercado Extra', valor: -245.67 },
      { data: '2023-11-27', descricao: 'Transferência recebida', valor: 1200.0 },
      { data: '2023-11-25', descricao: 'Netflix', valor: -39.9 },
      { data: '2023-11-24', descricao: 'Salário', valor: 4500.0 },
      { data: '2023-11-22', descricao: 'Restaurante', valor: -89.9 },
      { data: '2023-11-20', descricao: 'Farmácia', valor: -127.35 },
    ];

    // Dados para o gráfico
    this.dadosGrafico = [
      { categoria: 'Alimentação', valor: 850.45 },
      { categoria: 'Moradia', valor: 1200.0 },
      { categoria: 'Transporte', valor: 450.3 },
      { categoria: 'Lazer', valor: 320.75 },
      { categoria: 'Saúde', valor: 280.9 },
    ];

    // Dados para cartões de resumo
    this.cartoes = [
      { titulo: 'Receitas', valor: 5700.0, icone: 'trending_up' },
      { titulo: 'Despesas', valor: 2152.18, icone: 'trending_down' },
      { titulo: 'Economia', valor: 3547.82, icone: 'savings' },
    ];

    // Dados para metas financeiras
    this.metas = [
      { descricao: 'Férias', valorAtual: 3000, valorMeta: 5000, percentual: 60 },
      { descricao: 'Novo Notebook', valorAtual: 1500, valorMeta: 4000, percentual: 37.5 },
      { descricao: 'Reserva de emergência', valorAtual: 10000, valorMeta: 15000, percentual: 66.7 },
    ];

    this.carregandoSaldo = false;
    this.carregandoGrafico = false;
    this.carregandoTransacoes = false;
  }

  openTransactionModal() {
    this.showTransactionModal = true;
  }

  closeTransactionModal() {
    this.showTransactionModal = false;
  }

  handleTransactionSubmit(transaction: any) {
    console.log('Transação realizada:', transaction);
    this.showTransactionModal = false;

    // Aqui você chamaria o serviço para salvar a transação
    // this.transactionService.createTransaction(transaction).subscribe(...);

    // Por enquanto, apenas fechamos o modal
    this.closeTransactionModal();
  }

  // Método para formatar valores monetários
  formatarMoeda(valor: number): string {
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  // Método para determinar a classe CSS com base no valor
  getClasseValor(valor: number): string {
    return valor >= 0 ? 'valor-positivo' : 'valor-negativo';
  }
}
