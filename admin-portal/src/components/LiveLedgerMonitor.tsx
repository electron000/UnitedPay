import React, { useState, useEffect } from 'react';

interface LiveTransaction {
  id: string;
  utr: string;
  amount: number;
  payerVpa: string;
  payeeVpa: string;
  status: 'SETTLED' | 'PROCESSING' | 'FAILED' | 'REVERSED';
  latencyMs: number;
  timestamp: string;
}

export const LiveLedgerMonitor: React.FC = () => {
  const [tps, setTps] = useState<number>(142);
  const [successRate, setSuccessRate] = useState<number>(99.4);
  const [transactions, setTransactions] = useState<LiveTransaction[]>([
    {
      id: "ord_984128",
      utr: "425918239012",
      amount: 500.0,
      payerVpa: "rahul@unitedpay",
      payeeVpa: "priya@unitedpay",
      status: "SETTLED",
      latencyMs: 180,
      timestamp: "12:35:42"
    },
    {
      id: "ord_984127",
      utr: "425918239011",
      amount: 1450.0,
      payerVpa: "rahul@unitedpay",
      payeeVpa: "apdcl.bill@sbi",
      status: "SETTLED",
      latencyMs: 240,
      timestamp: "12:35:38"
    },
    {
      id: "ord_984126",
      utr: "425918239010",
      amount: 25000.0,
      payerVpa: "merchant_tea@unitedpay",
      payeeVpa: "supplier@hdfc",
      status: "SETTLED",
      latencyMs: 310,
      timestamp: "12:35:15"
    }
  ]);

  return (
    <div className="p-6 bg-slate-950 text-slate-100 min-h-screen font-sans">
      {/* Top Metrics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Real-Time Throughput</span>
          <div className="text-2xl font-bold text-blue-400 mt-1">{tps} TPS</div>
          <span className="text-[11px] text-emerald-400">● Peak 280 TPS</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Success Rate (NPCI)</span>
          <div className="text-2xl font-bold text-emerald-400 mt-1">{successRate}%</div>
          <span className="text-[11px] text-slate-400">Target SLA: 99.0%</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Average Latency</span>
          <div className="text-2xl font-bold text-slate-100 mt-1">210 ms</div>
          <span className="text-[11px] text-emerald-400">Fast Rail Active</span>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
          <span className="text-xs text-slate-400 font-semibold uppercase">Unresolved Timeouts</span>
          <div className="text-2xl font-bold text-emerald-400 mt-1">0</div>
          <span className="text-[11px] text-emerald-400">All Re-queried</span>
        </div>
      </div>

      {/* Live Transaction Ledger Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-bold text-white">Live Transaction Ledger</h2>
            <p className="text-xs text-slate-400">Real-time WebSocket feed from United Pay Core Switch & SDK Webhooks</p>
          </div>
          <input
            type="text"
            placeholder="Search by UTR or VPA..."
            className="bg-slate-950 border border-slate-700 rounded-xl px-3 py-1.5 text-xs text-white focus:outline-none focus:border-blue-500"
          />
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400">
                <th className="py-2.5 px-3">Time</th>
                <th className="py-2.5 px-3">Order ID</th>
                <th className="py-2.5 px-3">UTR Reference</th>
                <th className="py-2.5 px-3">Payer VPA</th>
                <th className="py-2.5 px-3">Payee VPA</th>
                <th className="py-2.5 px-3">Amount</th>
                <th className="py-2.5 px-3">Latency</th>
                <th className="py-2.5 px-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-300">
              {transactions.map((tx) => (
                <tr key={tx.id} className="hover:bg-slate-800/40 transition">
                  <td className="py-3 px-3 font-mono text-slate-400">{tx.timestamp}</td>
                  <td className="py-3 px-3 font-mono text-blue-400">{tx.id}</td>
                  <td className="py-3 px-3 font-mono font-bold text-white">{tx.utr}</td>
                  <td className="py-3 px-3">{tx.payerVpa}</td>
                  <td className="py-3 px-3">{tx.payeeVpa}</td>
                  <td className="py-3 px-3 font-bold text-white">₹{tx.amount.toFixed(2)}</td>
                  <td className="py-3 px-3 font-mono text-slate-400">{tx.latencyMs}ms</td>
                  <td className="py-3 px-3">
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                      {tx.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
