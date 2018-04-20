#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<math.h>

void findSolution(int, int **);
int checkPossible(int *, char*, int, int, int, int **);
int compare(const void*, const void*);
int inSubgrid(int, int, int*);
void getSubgrid(int, int, int, int, int **, int*);

int main(){
  FILE *fp;
  fp = fopen("input.txt","r");
  int i, j, k, n, counter=0;
  fscanf(fp,"%d",&n);
  char temp[250], *token, delim[2]=" ";
  int subgridSize[n];
  int **puzzles[n];

  for(i=0; i<n; i++){
    fscanf(fp,"%d\n",&subgridSize[i]);
    int squaredLen = subgridSize[i]*subgridSize[i];
    puzzles[i] = (int**)malloc(sizeof(int*)*squaredLen);
    for(j=0; j<squaredLen; j++){
      counter = 0;
      puzzles[i][j] = (int*)malloc(sizeof(int)*squaredLen);
      fgets(temp, 250, fp);
      token = strtok(temp, delim);
      while(token!=NULL){
        puzzles[i][j][counter] = atoi(token);
        token = strtok(NULL, delim);
        counter++;
      }
    }
  }

  for(i=0; i<n; i++){
    for(j=0; j<subgridSize[i]*subgridSize[i]; j++){
      for(k=0; k<subgridSize[i]*subgridSize[i]; k++){
        printf("%d ", puzzles[i][j][k]);
      }
      printf("\n");
    }
    findSolution(subgridSize[i], puzzles[i]);
  }
}

void findSolution(int size, int **puzzle){
  int h, i, j, npossible, start, move, row, col, count=0, candidate, *candidates, *subgrids;
  int gridTotal = (int)pow(size, 4);    //Total slots in the puzzle
  int rowcolTotal = (int)pow(size, 2);  //Number of elements per move/column
  int noptions[gridTotal+2];
  int options[gridTotal+2][rowcolTotal+2];
  int **grid = puzzle, **numArr;
  char * solTypes[] = {"regular", "x", "y", "xy"};  //Four Types of Solution (Mutually Exclusive?)
  candidates = (int*) malloc(sizeof(int)*rowcolTotal);
  subgrids = (int*) malloc(sizeof(int)*rowcolTotal);
  numArr = (int**)malloc(sizeof(int*)*rowcolTotal);

  for(i=0; i<rowcolTotal; i++){
    numArr[i] = (int*) malloc(sizeof(int)*rowcolTotal);
    for(j=0; j<rowcolTotal; j++){
      numArr[i][j] = ++count;
    }
  }

  for(h=0; h<4; h++){
    if(solTypes[h] == "y" && rowcolTotal%2==0) continue;

    move = start = 0; 
    noptions[start] = 1;
    //Finding Solution with Backtracking
    while(noptions[start] > 0){
      if(noptions[move] > 0){
        move++;
        noptions[move]=0; //initialize new move-1

        if(move == gridTotal+1){ //solution found
          for(i=1;i<move;i++){
            printf("%2i",options[i][noptions[i]]);
            if(i%rowcolTotal == 0) printf("\n");
          }
          printf("\n");
        }
        else if(move == 1){
          npossible = checkPossible(candidates, solTypes[h], rowcolTotal, move/rowcolTotal, (move%rowcolTotal)-1, grid);

          if(npossible == 1){
            options[move][++noptions[move]] = candidates[0];
          }else{
            for(i=1; i<npossible; i++){
              options[move][++noptions[move]] = candidates[i-1];
            }
          }
        }
        else{
          if(move % rowcolTotal == 0){
            row = (move/rowcolTotal)-1; col = rowcolTotal-1;
            npossible = checkPossible(candidates, solTypes[h], rowcolTotal, row, col, grid);
          }else{
            row = move/rowcolTotal; col = (move%rowcolTotal)-1;
            npossible = checkPossible(candidates, solTypes[h], rowcolTotal, row, col, grid);
          }

          getSubgrid(move, row, col, size, numArr, subgrids);

          if(npossible == 1){
            options[move][++noptions[move]] = candidates[0];
          }else{
            for(i=1; i<npossible; i++){
              for(j=move-1; j>=1; j--){
                if(inSubgrid(j, rowcolTotal, subgrids)){
                  if(candidates[i-1] == options[j][noptions[j]]) break;
                }
                if(move%rowcolTotal == 0){
                  if(j>(move-rowcolTotal) || (j == (move-rowcolTotal))){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }else if(move%rowcolTotal == 1){
                  if((move-j)%rowcolTotal == 0){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }else{
                  if((j%rowcolTotal!=0 && j >= move-rowcolTotal) || ((move-j)%rowcolTotal == 0)){
                    if(candidates[i-1] == options[j][noptions[j]]) break;
                  }
                }
              }
              if(!(j>=1)){
                options[move][++noptions[move]] = candidates[i-1];
              }
            }
          }
        }
        // printf("\n\nMOVE %d\n", move);
        // for(i=0; i<gridTotal+2; i++){
        //   printf("%d | ", i);
        //   for(j=0; j<rowcolTotal+2; j++){
        //     if(abs(options[i][j]) > 999){
        //       printf("_ ");
        //     }else{
        //       printf("%d ", options[i][j]);
        //     }
        //   }
        //   printf("\n");
        // }
        // printf("\n");
      }else{
        while(1){
          move--;
          noptions[move]--;
          if(move == start) break;
          if(move%rowcolTotal == 0 && grid[(move/rowcolTotal)-1][rowcolTotal-1]==0){
            // printf("move: %d noptions: %d\n", move, noptions[move]);
            break;
          }else if(grid[(move/rowcolTotal)][(move%rowcolTotal)-1]==0){
            break;
          }
        }
      }
    }
    //Break kasi pang regular check palang
    free(candidates); free(subgrids);
    for(i=0; i<rowcolTotal; i++){
      free(numArr[i]);
    }
    free(numArr);
    break;
  }
}

int inSubgrid(int x, int size, int* subgrids){
  int i;
  for(i=0; i<size; i++){
    if(subgrids[i] == x) return 1;
  }
  return 0;
}

void getSubgrid(int move, int row, int col, int subgridSize, int ** numArr, int * subgrids){
  int startIndRow = (row/subgridSize)*subgridSize;
  int endIndRow = (startIndRow+subgridSize);
  int startIndCol = (col/subgridSize)*subgridSize;
  int endIndCol = (startIndCol+subgridSize);
  int i, j, counter=0;

  for(i=startIndRow; i<endIndRow; i++){
    for(j=startIndCol; j<endIndCol; j++){
      subgrids[counter] = numArr[i][j];
      counter++;
    }
  }
}

int checkPossible(int *candidates, char * solutionType, int size, int row, int col, int ** grid){
  int i, j, k, flag, counter=0, subgridSize, startIndRow, endIndRow, startIndCol, endIndCol;
  subgridSize = sqrt(size);
  startIndRow = (row/subgridSize)*subgridSize;
  endIndRow = (startIndRow+subgridSize);
  startIndCol = (col/subgridSize)*subgridSize;
  endIndCol = (startIndCol+subgridSize);

  if(grid[row][col] != 0){
    *(candidates+counter) = grid[row][col];
  }else{
    //Initialize everything to -1 and use it as indicator of true candidates
    for(i=0; i<size; i++) candidates[i] = -1;

    for(i=size; i>0; i--){    //PRESORTS IN DESCENDING ORDER
      flag = 1;
      for(j=0; j<size; j++){
        if(solutionType == "regular"){
          if(grid[row][j] == i || grid[j][col] == i){
            flag = 0;
            break;
          }
        }else if(solutionType == "x"){

        }else if(solutionType == "y"){
          
        }else if(solutionType == "xy"){

        }
      }
      for(k=startIndRow; k<endIndRow; k++){
        for(j=startIndCol; j<endIndCol; j++){
          if(grid[k][j] == i){
            flag = 0;
            break;
          }
        }
        if(flag==0) break;
      }
      if(flag == 1){
        *(candidates+counter) = i;
        counter++;
      }
    }
  }

  return counter+1;
}