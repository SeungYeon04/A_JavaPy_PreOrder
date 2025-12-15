from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns

plt.switch_backend("Agg")


def run(master_path: str, outdir: Path) -> None:
    df = pd.read_csv(master_path)
    df = df.fillna({"recycled_kg": 0, "generated_kg": 0})
    df["rate"] = df["recycled_kg"] / df["generated_kg"].replace(0, pd.NA)

    region = (
        df.groupby("region_name")["rate"]
        .mean()
        .dropna()
        .sort_values(ascending=False)
    )
    plot_barh(region.head(5), region.tail(5), outdir / "chart_region.png", title="지역별 재활용률")

    material = (
        df.groupby("material_code")["rate"]
        .mean()
        .dropna()
        .sort_values(ascending=False)
    )
    plot_bar(material, outdir / "chart_material.png", title="재질별 재활용률")

    monthly = (
        df.groupby("year_month")["rate"]
        .mean()
        .dropna()
        .reset_index()
        .sort_values("year_month")
    )
    plot_line(monthly, outdir / "chart_trend.png", title="월별 재활용률 추이")


def plot_barh(top, bottom, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12, 6))
    data = pd.concat([top, bottom])
    sns.barplot(y=data.index, x=data.values, ax=ax, palette="viridis")
    ax.set_title(title)
    ax.set_xlabel("재활용률")
    ax.set_ylabel("")
    fig.tight_layout()
    fig.savefig(path, dpi=150)


def plot_bar(series, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12, 6))
    sns.barplot(x=series.index, y=series.values, ax=ax, palette="viridis")
    ax.set_title(title)
    ax.set_ylabel("재활용률")
    ax.set_xlabel("")
    fig.tight_layout()
    fig.savefig(path, dpi=150)


def plot_line(df, path, title: str) -> None:
    fig, ax = plt.subplots(figsize=(12, 6))
    sns.lineplot(data=df, x="year_month", y="rate", marker="o", ax=ax)
    ax.set_title(title)
    ax.set_ylabel("재활용률")
    ax.set_xlabel("월")
    ax.tick_params(axis="x", rotation=45)
    fig.tight_layout()
    fig.savefig(path, dpi=150)

