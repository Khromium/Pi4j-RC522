# Pi4j-RC522
Raspberry pi でNFCを読む為のドライバ

# 既知の問題
- Raspberry pi2の最新カーネルで動かない  
→[カーネルをダウングレードする](https://qiita.com/jollyjoester/items/ba59e5d43e28b701f120)
- anticollが上手く動かない。  
→コードはPythonと同じなのでpi4jの問題の可能性がある。
- 連続して値を読むと一回毎にNFC離さないといけない。連続すると一回ごとにIDが0の物が来る  
→原因調査中
