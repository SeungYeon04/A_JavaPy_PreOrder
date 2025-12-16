import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

# 한글 폰트 설정 (Windows 환경: Malgun Gothic 우선 등록)
import matplotlib
import matplotlib.font_manager as fm
try:
    win_fonts = [p for p in fm.findSystemFonts() if 'malgun' in p.lower()]
    if win_fonts:
        for p in win_fonts:
            fm.fontManager.addfont(p)
        matplotlib.rcParams['font.family'] = 'Malgun Gothic'
    else:
        matplotlib.rcParams['font.family'] = 'sans-serif'
    matplotlib.rcParams['axes.unicode_minus'] = False
except Exception:
    pass

# 전역 확대 스케일
SCALE = 3

# 전역 폰트/스타일 확대 (SCALE에 비례)
BASE_FONT = 10 * SCALE
matplotlib.rcParams['font.size'] = BASE_FONT
matplotlib.rcParams['axes.titlesize'] = int(BASE_FONT * 1.2)
matplotlib.rcParams['axes.labelsize'] = int(BASE_FONT * 1.1)
matplotlib.rcParams['xtick.labelsize'] = int(BASE_FONT * 0.9)
matplotlib.rcParams['ytick.labelsize'] = int(BASE_FONT * 0.9)
matplotlib.rcParams['legend.fontsize'] = int(BASE_FONT * 0.9)

# 인코딩 문제 대응 (한글 CSV는 euc-kr/CP949 계열이 우선)
ENCODINGS = ['euc-kr', 'cp949', 'utf-8']

def try_read_csv(path):
    for enc in ENCODINGS:
        try:
            return pd.read_csv(path, encoding=enc)
        except Exception:
            continue
    raise RuntimeError(f"{path} 파일 읽기 실패 (인코딩 재확인 필요)")
def analyze_jb_recycle(path, outdir):
    df = try_read_csv(path)
    df.columns = [c.strip() for c in df.columns]
    # (첫 행 한글 깨짐시 임의 컬럼명 지정)
    # 컬럼명이 깨지거나 예상명과 다를 경우 인덱스 기반으로 안전하게 재명명
    cols = df.columns.tolist()
    if not cols or not cols[0] or not str(cols[0])[0].isalnum():
        # 기본 구조가 예상과 다르면 최소한의 컬럼명 할당
        df.columns = ['구분','재활용','일반','음식','기타']
        cols = df.columns.tolist()
    # 구분 컬럼 보장
    if '구분' not in cols:
        df = df.rename(columns={cols[0]: '구분'})
        cols = df.columns.tolist()
    # 재활용 컬럼 찾아서 재명명(키워드 기반 또는 인덱스 기반)
    recy_col = None
    for c in cols[1:]:
        if isinstance(c, str) and ('재활' in c or '재활용' in c):
            recy_col = c
            break
    if recy_col is None and len(cols) > 1:
        recy_col = cols[1]
    if recy_col is not None and recy_col != '재활용':
        df = df.rename(columns={recy_col: '재활용'})
    # 주요 그룹별 박스플롯/바차트
    df_melt = df.melt(id_vars='구분', var_name='분리배출', value_name='비율')
    df_melt['비율'] = pd.to_numeric(df_melt['비율'], errors='coerce')
    plt.figure(figsize=(10 * SCALE,6 * SCALE))
    sns.boxplot(data=df_melt, x='분리배출', y='비율')
    plt.title('분리배출 유형별 비율 - 전체 그룹')
    plt.tight_layout()
    plt.savefig(outdir/'jb_boxplot.png', dpi=150, bbox_inches='tight')
    plt.close()
    # 상위/하위 5개 구분별 재활용(막대)
    df_sorted = df[['구분','재활용']].copy()
    df_sorted['재활용'] = pd.to_numeric(df_sorted['재활용'], errors='coerce')
    top5 = df_sorted.sort_values('재활용', ascending=False).head(5)
    bottom5 = df_sorted.sort_values('재활용').head(5)
    plt.figure(figsize=(10 * SCALE, 5 * SCALE))
    sns.barplot(data=top5, x='구분', y='재활용')
    plt.title('재활용 비율 Top5 (구분)')
    plt.tight_layout()
    plt.savefig(outdir/'jb_top5_bar.png', dpi=150, bbox_inches='tight')
    plt.close()
    # 저장: 요약 CSV (정렬된 재활용 비율표)
    try:
        df_sorted.to_csv(outdir / 'jb_rank_by_recycle.csv', index=False, encoding='utf-8-sig')
    except Exception:
        pass
    plt.figure(figsize=(10 * SCALE, 5 * SCALE))
    sns.barplot(data=bottom5, x='구분', y='재활용')
    plt.title('재활용 비율 하위5 (구분)')
    plt.tight_layout()
    plt.savefig(outdir/'jb_bottom5_bar.png', dpi=150, bbox_inches='tight')
    plt.close()

# 2. 한국환경공단_재활용가능자원.CSV (품목별 배출량 등)
def analyze_env_item(path, outdir):
    df = try_read_csv(path)
    df.columns = [c.strip() for c in df.columns]
    key_name = df.columns[1] # 두번째 컬럼이 보통 품목명
    val_cols = df.columns[2:-1] # 중간수치만
    # 합계 계산: 중간 수치 컬럼들을 합산
    try:
        numeric = df[val_cols].apply(pd.to_numeric, errors='coerce')
        item_sum = numeric.sum(axis=1)
        item_sum = pd.DataFrame({"품목": df[key_name], "배출량합계": item_sum}).groupby('품목').sum().sort_values('배출량합계', ascending=False).reset_index()
    except Exception:
        item_sum = pd.DataFrame({"품목": df[key_name], "배출량합계": 0})
    plt.figure(figsize=(10 * SCALE, 5 * SCALE))
    sns.barplot(data=item_sum.head(10), x='품목', y='배출량합계')
    plt.title('상위 10개 품목 배출합계')
    plt.tight_layout()
    plt.savefig(outdir/'env_item_top10_bar.png', dpi=150, bbox_inches='tight')
    plt.close()
    # 저장: 요약 CSV
    try:
        item_sum.to_csv(outdir / 'env_item_summary.csv', index=False, encoding='utf-8-sig')
    except Exception:
        pass
    # 전체 품목 비율 파이
    plt.figure(figsize=(8 * SCALE, 8 * SCALE))
    plt.pie(item_sum.head(10)['배출량합계'], labels=item_sum.head(10)['품목'], autopct='%1.1f%%', startangle=140)
    plt.title('품목별 배출량 비율(상위10)')
    plt.savefig(outdir/'env_item_top10_pie.png', dpi=150, bbox_inches='tight')
    plt.close()
    # (removed misplaced operator/product summaries - handled in analyze_env_operator)

# 3. 한국환경공단_재활용지정사업자 재활용실적_20250930.CSV (사업자/품목별)
def analyze_env_operator(path, outdir):
    df = try_read_csv(path)
    df.columns = [c.strip() for c in df.columns]
    biz_name = df.columns[1]
    product_name = df.columns[2]
    # 컬럼 자동 검출: 사업자명(범주형), 품목명(범주형), 실적/배출량(수치형)을 찾습니다.
    import numpy as np
    # 후보: 범주형 컬럼들
    obj_cols = [c for c in df.columns if df[c].dtype == object or df[c].dtype == 'O']
    # 우선 컬럼명 키워드 기반으로 사업자명/품목명(폐자원)을 찾도록 강화
    biz_keywords = ['사업자', '업체', '업체수', '사업자별', '사업자명']
    prod_keywords = ['폐자원', '품목', '품목명', '폐지', '폐골판지', '폐유리', '품목별', '폐플라스틱']
    found_biz = None
    found_prod = None
    for c in df.columns:
        lc = str(c).lower()
        for k in biz_keywords:
            if k in lc:
                found_biz = c
                break
        for k in prod_keywords:
            if k in lc:
                found_prod = c
                break
        if found_biz and found_prod:
            break
    if found_biz:
        biz_name = found_biz
    if found_prod:
        product_name = found_prod

    # fallback: heuristic based on unique counts if any still None
    if obj_cols:
        if not found_biz:
            biz_name = max(obj_cols, key=lambda c: df[c].nunique())
        if not found_prod:
            prod_candidates = [c for c in obj_cols if c != biz_name]
            product_name = prod_candidates[0] if prod_candidates else biz_name
    else:
        # 범주형이 없다면 첫 두 컬럼을 사용
        biz_name = df.columns[0]
        product_name = df.columns[1] if len(df.columns) > 1 else df.columns[0]

    # 수치형 컬럼 찾기 - 명시적 후보명을 우선 사용하도록 강화
    preferred_names = ['총 폐자원사용량(톤)', '제품생산량(톤)', '총 폐자원사용량', '제품생산량', '총실적', '실적', '합계', '폐자원사용량(톤)']
    # 정규화된 컬럼명 맵(원본 -> normalized)
    norm_map = {c: ''.join(ch for ch in str(c).lower() if ch.isalnum()) for c in df.columns}
    amount_col = None
    # 1) 정확히 일치하는 후보 찾기
    for name in preferred_names:
        for c in df.columns:
            if str(c).strip() == name:
                amount_col = c
                break
        if amount_col:
            break
    # 2) 포함/유사 키워드로 찾기
    if amount_col is None:
        for name in preferred_names:
            key = ''.join(ch for ch in name.lower() if ch.isalnum())
            for c, nc in norm_map.items():
                if key in nc:
                    amount_col = c
                    break
            if amount_col:
                break
    # 3) 기존 방식: 수치형 컬럼 우선 선택
    if amount_col is None:
        num_cols = df.select_dtypes(include=[np.number]).columns.tolist()
        if num_cols:
            # non-null 값이 많은 컬럼 선택
            amount_col = max(num_cols, key=lambda c: df[c].notnull().sum())
        else:
            # 모든 컬럼을 수치로 변환해 가장 유효한 칼럼 선택
            best = None
            best_count = -1
            for c in df.columns:
                coerced = pd.to_numeric(df[c].astype(str).str.replace(',',''), errors='coerce')
                cnt = coerced.notnull().sum()
                if cnt > best_count:
                    best_count = cnt
                    best = c
            amount_col = best

    # 안전한 수치 변환
    df[amount_col] = pd.to_numeric(df[amount_col].astype(str).str.replace(',',''), errors='coerce')
    df[amount_col] = df[amount_col].fillna(0.0)
    # 사업자별 총합 상위 5
    op_sum = df.groupby(biz_name)[amount_col].sum().sort_values(ascending=False).head(5).reset_index()
    # 수평 바 차트로 그려서 막대가 겹치지 않게 함
    plt.figure(figsize=(12 * SCALE, 6 * SCALE))
    ax = plt.gca()
    names = op_sum[biz_name].astype(str)
    values = op_sum[amount_col].astype(float)
    y_pos = range(len(names))
    bars = ax.barh(y_pos, values, align='center', color=sns.color_palette('deep', n_colors=max(1,len(names))), edgecolor='black', height=0.6)
    ax.set_yticks(y_pos)
    ax.set_yticklabels(names)
    ax.invert_yaxis()  # 가장 큰값이 위로 오게
    ax.set_xlabel('총실적')
    plt.title('사업자별 실적 TOP5')
    # x축 값 포맷(천 단위 쉼표)
    import matplotlib.ticker as mtick
    ax.xaxis.set_major_formatter(mtick.FuncFormatter(lambda x, p: format(int(x), ',')))
    # 레이블(숫자) 표시
    for b in bars:
        w = b.get_width()
        ax.text(w + max(values) * 0.01, b.get_y() + b.get_height() / 2, f"{int(w):,}", va='center')
    plt.tight_layout()
    plt.savefig(outdir/'env_operator_top5.png', dpi=150, bbox_inches='tight')
    plt.close()
    # 저장: 사업자/품목 요약 CSV
    try:
        op_all = df.groupby(biz_name)[amount_col].sum().reset_index().rename(columns={amount_col: '총실적'})
        prod_all = df.groupby(product_name)[amount_col].sum().reset_index().rename(columns={amount_col: '총실적'})
        op_all.to_csv(outdir / 'env_operator_summary.csv', index=False, encoding='utf-8-sig')
        prod_all.to_csv(outdir / 'env_product_summary.csv', index=False, encoding='utf-8-sig')
    except Exception:
        pass
    # 품목별 총합 상위 5
    prod_sum = df.groupby(product_name)[amount_col].sum().sort_values(ascending=False).head(5).reset_index()
    prod_sum[amount_col] = pd.to_numeric(prod_sum[amount_col], errors='coerce')
    # 수평 막대 차트로 그려서 품목명이 길어도 보이게 함
    plt.figure(figsize=(12 * SCALE, 6 * SCALE))
    ax = plt.gca()
    names = prod_sum[product_name].astype(str)
    values = prod_sum[amount_col].astype(float)
    y_pos = range(len(names))
    bars = ax.barh(y_pos, values, align='center', color=sns.color_palette('deep', n_colors=max(1,len(names))), edgecolor='black', height=0.6)
    ax.set_yticks(y_pos)
    ax.set_yticklabels(names)
    ax.invert_yaxis()
    ax.set_xlabel('총실적 (톤)')
    plt.title('품목별 실적 TOP5')
    # 좌측 여백을 넉넉히 줘서 긴 라벨을 수용
    plt.subplots_adjust(left=0.30, right=0.98, top=0.92, bottom=0.10)
    import matplotlib.ticker as mtick
    ax.xaxis.set_major_formatter(mtick.FuncFormatter(lambda x, p: format(int(x), ',')))
    for b in bars:
        w = b.get_width()
        ax.text(w + max(values) * 0.01, b.get_y() + b.get_height() / 2, f"{int(w):,}", va='center', fontsize=int(BASE_FONT * 0.9))
    plt.tight_layout()
    plt.savefig(outdir/'env_product_top5.png', dpi=150, bbox_inches='tight')
    plt.close()

def main():
    # 기준 경로를 파일 위치 기준으로 삼아 레포지토리 구조 변경에 강하게 만듭니다.
    base = Path(__file__).resolve().parent
    # 데이터 폴더는 레포지토리 구조에서 SpringWeb 상위가 아닌, SpringWeb과 같은 계층에서 찾습니다.
    # base.parents[0]는 SpringWeb 디렉토리를 가리키므로, 그 아래의 상대경로를 사용합니다.
    data_dir = base.parents[0] / '..' / '데이터들'
    # 웹에서 정적 파일로 바로 제공하려면 SpringBoot의 static 폴더 하위에 저장합니다 (SpringWeb/src/...).
    output_dir = base.parents[0] / 'src' / 'main' / 'resources' / 'static' / 'analysis_outputs'
    output_dir.mkdir(parents=True, exist_ok=True)

    analyze_jb_recycle(data_dir / '전북특별자치도_재활용품 배출 현황_20081231.CSV', output_dir)
    analyze_env_item(data_dir / '한국환경공단_재활용가능자원.CSV', output_dir)
    analyze_env_operator(data_dir / '한국환경공단_재활용지정사업자 재활용실적_20250930.CSV', output_dir)
    print(f'모든 분석 그래프/표가 {output_dir}에 저장되었습니다.')

if __name__ == '__main__':
    main()

