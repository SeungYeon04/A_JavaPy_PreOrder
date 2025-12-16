from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

plt.switch_backend("Agg")

# 전역 확대 비율: 3배
SCALE = 3

# 한글 폰트 설정 (Windows 환경에서 Malgun Gothic 우선)
import matplotlib
try:
    matplotlib.rcParams['font.family'] = 'Malgun Gothic'
except Exception:
    pass


def run(master_path: str, outdir: Path) -> None:
    df = pd.read_csv(master_path)
    # 안전: 컬럼 존재 여부에 따라 집계/차트 생성
    df = df.copy()
    # 기본 치환
    if "recycled_kg" in df.columns and "generated_kg" in df.columns:
        df = df.fillna({"recycled_kg": 0, "generated_kg": 0})
        # 비율 계산: 0 나누기 방지
        df["rate"] = df["recycled_kg"] / df["generated_kg"].replace(0, pd.NA)
    else:
        # rate 칼럼이 이미 있거나 계산 불가하면 시도해보기
        if "rate" not in df.columns:
            df["rate"] = pd.NA

    # 지역별
    if "region_name" in df.columns:
        region = (
            df.groupby("region_name")["rate"]
            .mean()
            .dropna()
            .sort_values(ascending=False)
        )
        if not region.empty:
            plot_barh(region.head(5), region.tail(5), outdir / "chart_region.png", title="지역별 재활용률")

    # 재질(품목/재질)별
    # 지원하는 칼럼명 중 사용 가능한 것을 우선 선택
    material_col = None
    for c in ("material_code", "material", "item", "품목", "재질"):
        if c in df.columns:
            material_col = c
            break
    if material_col is not None:
        material = (
            df.groupby(material_col)["rate"]
            .mean()
            .dropna()
            .sort_values(ascending=False)
        )
        if not material.empty:
            plot_bar(material, outdir / "chart_material.png", title=f"{material_col}별 재활용률")
            # 파이 차트(상위10)
            top = material.head(10)
            plt.figure(figsize=(8 * SCALE, 8 * SCALE))
            plt.pie(top.values, labels=top.index, autopct="%1.1f%%", startangle=140)
            plt.title(f"{material_col}별 재활용률(상위10)")
            plt.savefig(outdir / "chart_material_top10_pie.png", dpi=150)
            plt.close()

    # 시간(추세)
    time_col = None
    for c in ("year_month", "date", "year", "month"):
        if c in df.columns:
            time_col = c
            break
    if time_col is not None and "rate" in df.columns:
        monthly = (
            df.groupby(time_col)["rate"]
            .mean()
            .dropna()
            .reset_index()
        )
        if not monthly.empty:
            # 정렬: 가능한 경우 날짜 형식으로
            try:
                monthly = monthly.sort_values(time_col)
            except Exception:
                pass
            plot_line(monthly, outdir / "chart_trend.png", title="월별 재활용률 추이")

    # 박스플롯: 재활용률 분포를 그룹별로(재질 또는 지역)
    if "rate" in df.columns and material_col is not None:
        df_box = df[[material_col, "rate"]].dropna()
        if not df_box.empty:
            plt.figure(figsize=(10 * SCALE, 6 * SCALE))
            sns.boxplot(data=df_box, x=material_col, y="rate")
            plt.title(f"{material_col}별 재활용률 분포")
            plt.tight_layout()
            plt.savefig(outdir / "chart_material_boxplot.png", dpi=150)
            plt.close()


def plot_barh(top, bottom, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12 * SCALE, 6 * SCALE))
    data = pd.concat([top, bottom])
    sns.barplot(y=data.index, x=data.values, ax=ax, palette="viridis")
    ax.set_title(title)
    ax.set_xlabel("재활용률")
    ax.set_ylabel("")
    fig.tight_layout()
    fig.savefig(path, dpi=150)


def plot_bar(series, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12 * SCALE, 6 * SCALE))
    sns.barplot(x=series.index, y=series.values, ax=ax, palette="viridis")
    ax.set_title(title)
    ax.set_ylabel("재활용률")
    ax.set_xlabel("")
    fig.tight_layout()
    fig.savefig(path, dpi=150)


def plot_line(df, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12 * SCALE, 6 * SCALE))
    sns.lineplot(data=df, x="year_month", y="rate", marker="o", ax=ax)
    ax.set_title(title)
    ax.set_ylabel("재활용률")
    ax.set_xlabel("월")
    ax.tick_params(axis="x", rotation=45)
    fig.tight_layout()
    fig.savefig(path, dpi=150)

