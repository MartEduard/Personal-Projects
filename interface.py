# interface.py

HTML_PAGE = """<!DOCTYPE html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0"><title>Safety Monitor</title><script src="https://cdn.jsdelivr.net/npm/chart.js"></script><style>
:root{--bg:#121212;--card:#1e1e1e;--text:#e0e0e0;--accent:#bb86fc;--dang:#cf6679;--warn:#ffb74d;--ok:#03dac6}
body{font-family:'Segoe UI',sans-serif;background:var(--bg);color:var(--text);text-align:center;margin:0;padding:10px}
.card{background:var(--card);padding:20px;border-radius:15px;margin-bottom:15px;box-shadow:0 4px 10px rgba(0,0,0,0.5)}
.val{font-size:2.5rem;font-weight:700;color:var(--accent)}.lbl{color:#888;font-size:0.8rem;letter-spacing:1px}
.st-box{padding:12px;border-radius:8px;font-weight:bold;margin-bottom:15px;background:#333;color:#fff;transition:all 0.3s}
.ok{background:rgba(3,218,198,0.2);color:var(--ok);border:1px solid var(--ok)}
.warn{background:rgba(255,183,77,0.2);color:var(--warn);border:1px solid var(--warn)}
.dang{background:rgba(207,102,121,0.2);color:var(--dang);border:1px solid var(--dang);animation:pulse 1s infinite}
@keyframes pulse{0%{box-shadow:0 0 0 0 rgba(207,102,121,0.4)}70%{box-shadow:0 0 0 10px rgba(207,102,121,0)}}
canvas{max-height:300px;width:100%}
.grid{display:grid;grid-template-columns:1fr 1fr 1fr;gap:10px}
</style></head><body>
<div class="card">
 <div id="sb" class="st-box">CONECTARE...</div>
 <div class="val" id="g">0</div><div class="lbl">GAZ (PPM)</div>
 <hr style="border-color:#333;margin:15px 0">
 <div class="grid">
  <div><div class="val" style="font-size:1.4rem" id="t">--</div><div class="lbl">TEMP &deg;C</div></div>
  <div><div class="val" style="font-size:1.4rem" id="h">--</div><div class="lbl">UMID %</div></div>
  <div><div class="val" style="font-size:1.4rem" id="f">OFF</div><div class="lbl">FAN</div></div>
 </div>
 <p style="font-size:0.8rem;color:#666">Delta T: <span id="dt">--</span> | Delta H: <span id="dh">--</span></p>
</div>
<div class="card"><canvas id="ch"></canvas></div>
<script>
const ctx=document.getElementById('ch').getContext('2d');
Chart.defaults.color='#888';Chart.defaults.borderColor='#333';
const chart=new Chart(ctx,{type:'line',data:{labels:[],datasets:[
 {label:'Gaz',data:[],yAxisID:'y',borderColor:'#bb86fc',backgroundColor:'rgba(187,134,252,0.1)',fill:true,tension:0.4,pointRadius:1},
 {label:'Temp',data:[],yAxisID:'y1',borderColor:'#cf6679',borderDash:[5,5],tension:0.4,pointRadius:0},
 {label:'Umid',data:[],yAxisID:'y1',borderColor:'#03dac6',tension:0.4,pointRadius:0}
]},options:{animation:false,interaction:{mode:'index',intersect:false},scales:{x:{ticks:{maxTicksLimit:6}},y:{position:'left',grid:{color:'#333'}},y1:{position:'right',grid:{drawOnChartArea:false}}}}});
function upd(d){
 document.getElementById('g').innerText=d.gaz;
 document.getElementById('t').innerText=d.temp.toFixed(1);
 document.getElementById('h').innerText=d.hum.toFixed(1);
 document.getElementById('f').innerText=d.fan;
 document.getElementById('dt').innerText=(d.dt>0?"+":"")+d.dt.toFixed(1);
 document.getElementById('dh').innerText=d.dh.toFixed(1);
 const sb=document.getElementById('sb');
 sb.innerText=d.status;sb.className='st-box';
 if(d.status.includes('PERICOL')||d.status.includes('INCENDIU')) sb.classList.add('dang');
 else if(d.status.includes('ATENTIE')||d.status.includes('RACIRE')||d.status.includes('SUPRA')) sb.classList.add('warn');
 else sb.classList.add('ok');
 const n=new Date();
 const ts=n.getHours()+":"+(n.getMinutes()<10?'0':'')+n.getMinutes()+":"+(n.getSeconds()<10?'0':'')+n.getSeconds();
 if(chart.data.labels.length>30){chart.data.labels.shift();chart.data.datasets.forEach(ds=>ds.data.shift())}
 chart.data.labels.push(ts);
 chart.data.datasets[0].data.push(d.gaz);
 chart.data.datasets[1].data.push(d.temp);
 chart.data.datasets[2].data.push(d.hum);
 chart.update();
}
setInterval(()=>{fetch('/data').then(r=>r.json()).then(d=>upd(d))},3000);
</script></body></html>"""
