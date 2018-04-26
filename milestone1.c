#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

void findSolution(int, int **);
int checkPossible(int *, char*, int, int, int, int **);
int compare(const void*, const void*);
int inSubgrid(int, int, int*);
void getSubgrid(int, int, int, int, int **, int*);
int inXGrids(int, int, int, int **, int);
FILE *f;

int main(){
  FILE *fp;
  fp = fopen("input.txt","r");
  f = fopen("output.txt","w");
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
    printf("\nPUZZLE %d\n",(i+1));
    fprintf(f,"\nPUZZLE %d\n",(i+1));
    for(j=0; j<subgridSize[i]*subgridSize[i]; j++){
      for(k=0; k<subgridSize[i]*subgridSize[i]; k++){
        printf("%d ", puzzles[i][j][k]);
        fprintf(f,"%d ",puzzles[i][j][k]);
      }
      printf("\n");
      fprintf(f,"\n");
    }
    findSolution(subgridSize[i], puzzles[i]);
  }
  fclose(f);
}

void findSolution(int size, int **puzzle){
  int h, i, j, npossible, start, move, row, col, count=0, candidate, *candidates, *subgrids, temp;
  int gridTotal = (int)pow(size, 4);    //Total slots in the puzzle
  int rowcolTotal = (int)pow(size, 2);  //Number of elements per move/column
  int noptions[gridTotal+2];
  int options[gridTotal+2][rowcolTotal+2];
  int solCounter = 0;
  int **grid = puzzle, **numArr;
  char * solTypes[] = {"x", "regular", "y", "xy"};  //Four Types of Solution (Mutually Exclusive?)
  candidates = (int*) malloc(sizeof(int)*rowcolTotal);
  subgrids = (int*) malloc(sizeof(int)*rowcolTotal);
  numArr = (int**)malloc(sizeof(int*)*rowcolTotal);

  for(i=0; i<rowcolTotal; i++){
    numArr[i] = (int*) malloc(sizeof(int)*rowcolTotal);
    for(j=0; j<rowcolTotal; j++){
      numArr[i][j] = ++count;
    }
  }

  for(h=0; h<2; h++){
    if(solTypes[h] == "y" && rowcolTotal%2==0) continue;
    
    for (i=0;i<gridTotal+2;i++) {
      noptions[i] = 0;
    }

    for (i=0;i<gridTotal+2;i++) {
      for (j=0;j<rowcolTotal+2;j++) {
        options[i][j] = 0;
      }
    }

    printf("SOLUTION %s\n", solTypes[h]);
    solCounter = 0;
    move = start = 0; 
    noptions[start] = 1;
    //Finding Solution with Backtracking
    while(noptions[start] > 0){
      if(noptions[move] > 0){
        move++;
        noptions[move]=0; //initialize new move-1

        if(move == gridTotal+1){ //solution found
          printf("SOLUTION %d\n",(solCounter+1));
          fprintf(f,"SOLUTION %d\n",(solCounter+1));
          for(i=1;i<move;i++){
            printf("%2i",options[i][noptions[i]]);
            fprintf(f,"%2i",options[i][noptions[i]]);
            if(i%rowcolTotal == 0){
              printf("\n");
              fprintf(f,"\n");
            }
          }
          printf(" ");
          fprintf(f," ");

          solCounter++;
          move--; //go back to the last cell
          noptions[move]--; //pop
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

          // printf("POSSIBLE: ");
          // for(i=0; i<npossible-1; i++){
          //   printf("%d ", candidates[i]);
          // }
          // printf("\n");

          getSubgrid(move, row, col, size, numArr, subgrids);

          if(npossible == 1){
            options[move][++noptions[move]] = candidates[0];
          }else{
            for(i=1; i<npossible; i++){
              for(j=move-1; j>=1; j--){
                if(inSubgrid(j, rowcolTotal, subgrids)){
                  if(candidates[i-1] == options[j][noptions[j]]) {
                    break;
                  }
                }
                if(solTypes[h] == "x"){
                  if(inXGrids(j,row, col, numArr, rowcolTotal)){
                    if(candidates[i-1] == options[j][noptions[j]]) {
                      break;
                    }
                  } 
                }
                if(move%rowcolTotal == 0){ //last column
                  if(j>(move-rowcolTotal) || (move-j)%rowcolTotal == 0){
                    if(candidates[i-1] == options[j][noptions[j]]) {
                      break;
                    }
                  }
                }else if(move%rowcolTotal == 1){ //first column
                  if((move-j)%rowcolTotal == 0){
                    if(candidates[i-1] == options[j][noptions[j]]) {
                      break;
                    }
                  }
                }else{ //in between
                  int currentRow = move/rowcolTotal;
                  int leftmostIdx = (rowcolTotal * currentRow) + 1;

                  if (j >= leftmostIdx) { //same row
                    if(candidates[i-1] == options[j][noptions[j]]) break; 
                  }
                  else { //not in same row
                    if ((move-j)%rowcolTotal == 0) { //only check same column
                      if(candidates[i-1] == options[j][noptions[j]]) break;
                    }
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
        // scanf("%d", &temp);
      }
      else{ //current stack empty, pop previous stack
        move--;
        noptions[move]--;
      }
    }
    //Break kasi pang regular check palang
    // free(candidates); free(subgrids);
    // for(i=0; i<rowcolTotal; i++){
    //   free(numArr[i]);
    // }
    // free(numArr);
    // break;
  }
}

int inXGrids(int x, int row, int col, int ** numArr, int total){
  int i, j;
  for(i=0; i<total; i++){
    for(j=0; j<total; j++){  
      if(row==col && i==j){
        if(((i*total)+j+1) == x){
          return 1;
        }
      }else if((col+row) == (total-1) && ((i+j)==total-1)){
        if(((i*total)+j+1) == x){
          return 1;
        }
      }
    }
  }
  return 0;
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

int checkSubgrid(int val, int size, int row, int col, int **grid) {
  int startRow = (row/size)*size;
  int startCol = (col/size)*size;
  int i, j;

  for (i=startRow;i<startRow+size;i++) {
    for (j=startCol;j<startCol+size;j++) {
      if (grid[i][j] == val) {
        return 0;
      }
    }
  }
  return 1;
}

int checkPossible(int *candidates, char * solutionType, int size, int row, int col, int ** grid){
  int i, j, k, flag, counter=0, subgridSize;
  subgridSize = sqrt(size);

  if(grid[row][col] != 0){
    *(candidates+counter) = grid[row][col];
  }else{
    //Initialize everything to -1 and use it as indicator of true candidates
    for(i=0; i<size; i++) candidates[i] = -1;

    for(i=size; i>0; i--){    //PRESORTS IN DESCENDING ORDER
      flag = 1;

      flag = checkSubgrid(i, subgridSize, row, col, grid);
      if (flag == 0) continue;

      for(j=0; j<size; j++){
        if(solutionType == "regular" || solutionType == "x"){
          if(grid[row][j] == i || grid[j][col] == i) {
            flag = 0;
            break;
          }
        if(solutionType == "x"){
          for(k=0; k<size; k++){
            if(row == col){
              if(j==k){
                if(grid[j][k] == i){
                  flag = 0; break;
                }
              }else if((col+row) == (size-1)){
                if((j+k) == (size-1)){
                  if(grid[j][k] == i){
                    flag = 0; break;
                  }
                }
              }
            }
          }
        }

        if(solutionType == "y"){}
          
        if(solutionType == "xy"){}
        }
      }

      if(flag == 1){
        *(candidates+counter) = i;
        counter++;
      }
    }
  }

  return counter+1;
}