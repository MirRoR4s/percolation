package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * estimate system percolation threshold by simulator.
 */
public class Percolation {
    private final int N;
    // use a two dimentional boolean array to represent site.
    // if (i, j) site is true, it means this site is open, otherwise it's blocked.
    private final boolean[][] sites;
    private WeightedQuickUnionUF weightedQuickUnionUF;
    private WeightedQuickUnionUF weightedQuickUnionUFFullSite; // 为了 isFull 的正确性所需
    private int openSiteNum = 0;
    private final int[] dx = { -1, 0, 0, 1 };
    private final int[] dy = { 0, -1, 1, 0 };

    /**
     * creates N-by-N grid, with all sites initially blocked.
     * 
     * @throws java.lang.IllegalArgumentException if N less than 0.
     * @param N number of sites.
     */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N can't be less than 0.");
        }

        this.N = N;
        sites = new boolean[N][N];
        int siteNum = N * N;
        weightedQuickUnionUF = new WeightedQuickUnionUF(siteNum + 2);
        // weightedQuickUnionUFFullSite = new WeightedQuickUnionUF(siteNum + 2);

        /*
         * connect the virtual top site and the top row sites.
         * connect the virtual bottom site and the bottom row sites.
         * note: the index of the virtual top site is N * N.
         * note: the index of the virtual bottom site is N * N + 1.
         * for more details
         * see @https://docs.google.com/presentation/d/1AV5v-gTSIi5xUwtm-
         * FtkReUmuTA3Mqry1eGjje7OgQo/edit#slide=id.g11dd5164a7_2_260
         */
        int x = siteNum - N;
        for (int j = 0; j < N; j++) {
            weightedQuickUnionUF.union(siteNum, j);
            weightedQuickUnionUF.union(siteNum + 1, x + j);
            // weightedQuickUnionUFFullSite.union(N * N, j);
        }
    }

    /**
     * Open the site (row, col) if it is not open already.
     * 
     * @throws java.lang.IndexOutOfBoundsException if row or col greater than grid
     *                                             size.
     * @param row row coordinate.
     * @param col column coordinate.
     */
    public void open(int row, int col) {
        validate(row, col);

        if (!isOpen(row, col)) {
            sites[row][col] = true;
            openSiteNum++;
            connectNeighbours(row, col);
        }
    }

    /**
     * validate the coordinates of this site.
     * 
     * @param row the x coordinate of this site, start from 0.
     * @param col the y coordinate of this site, start from 0.
     */
    private void validate(int row, int col) {
        if (row < 0 || row >= N || col < 0 || col >= N) {
            throw new java.lang.IndexOutOfBoundsException(
                    "row or col greater than grid size " + N + ", row: " + row + " col: " + col);
        }
    }

    /**
     * is the site (row, col) open?
     * 
     * @param row the x coordinate of this site, start from 0.
     * @param col the y coordinate of this site, start from 0.
     * @return True if site (row, col) is open.
     */
    public boolean isOpen(int row, int col) {
        return sites[row][col];
    }

    /**
     * connect open site that coordinate is (row, col) to its neighbours.
     * 
     * @param row the x coordinate of this site, start from 0.
     * @param col the y coordinate of this site, start from 0.
     */
    private void connectNeighbours(int row, int col) {
        // 将当前 site 的二维坐标转为对应的一维坐标
        int p = xyTo1D(row, col);
        int newRow, newCol;
        for (int i = 0; i < 4; i++) {
            newRow = row + dx[i];
            newCol = col + dy[i];
            if (newRow < 0 || newRow >= N || newCol < 0 || newCol >= N) {
                continue;
            }
            if (isOpen(newRow, newCol)) {
                weightedQuickUnionUF.union(p, xyTo1D(newRow, newCol));

                weightedQuickUnionUFFullSite.union(p, xyTo1D(newRow, newCol));
            }
        }
    }

    /**
     * convert the two dimentional coordinate to one dimentional coordinate.
     * 
     * @param row
     * @param col
     * @return one dimentional coordinate.
     */
    private int xyTo1D(int row, int col) {
        return row * N + col;
    }

    /**
     * is the site (row, col) full?
     * 
     * @param row site 的行坐标，从 0 开始
     * @param col site 的列坐标，从 0 开始
     * @return True if site (row, col) is full.
     */
    public boolean isFull(int row, int col) {
        /*
         * A full site is an open site that can be connected to an open site in the
         * top row via a chain of neighboring (left, right, up, down) open sites.
         */
        if (!isOpen(row, col)) {
            return false;
        }
        int p = xyTo1D(row, col);
        return weightedQuickUnionUFFullSite.find(p) == weightedQuickUnionUFFullSite.find(N * N);
    }

    /**
     * number of open sites.
     * 
     * @return number of open sites.
     */
    public int numberOfOpenSites() {
        return openSiteNum;
    }

    /**
     * does the system percolate?
     * 
     * @return True when system percolate.
     */
    public boolean percolates() {
        if (openSiteNum == 0) {
            return false;
        }
        // 当顶部和底部的两虚拟节点连通时则系统是渗滤的
        return weightedQuickUnionUF.find(N * N) == weightedQuickUnionUF.find((N * N) + 1);
    }

    // test client (optional)
    // public static void main(String[] args) {
    // int n = 5;
    // Percolation percolation = new Percolation(n);
    // while (!percolation.percolates()) {
    // int row = StdRandom.uniform(n);
    // int col = StdRandom.uniform(n);
    // percolation.open(row, col);
    // }
    // }
}
