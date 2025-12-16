import pandas as pd
fn = r'D:/Github/A_JavaPy_PreOrder/데이터들/한국환경공단_재활용지정사업자 재활용실적_20250930.CSV'
encs = ['utf-8','cp949','euc-kr','latin1']
for e in encs:
    try:
        df = pd.read_csv(fn, encoding=e)
        print('ENC',e)
        print('COLS', df.columns.tolist())
        print(df.head(3).to_string())
    except Exception as ex:
        print('ERR',e,ex)
