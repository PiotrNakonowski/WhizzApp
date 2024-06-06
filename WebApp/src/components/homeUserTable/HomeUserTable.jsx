import * as React from 'react';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import "./homeUserTable.scss";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: '#616161',
    color: theme.palette.common.white,
    fontSize: 20,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 16,
    border: 0,
  },
}));

const StyledTableRow = styled(TableRow)(() => ({
  '&:nth-of-type(odd)': {
    backgroundColor: '#EDEDED',
  },
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const StyledTableHead = styled(TableHead)({
  backgroundColor: '#616161',
});

const StyledTableContainer = styled(TableContainer)({
  marginBottom: '40px',
});

const rows = [
  createData(1, 'John', 'Doe', 'john.doe@example.com', '2023-10-15'),
  createData(2, 'Jane', 'Smith', 'jane.smith@example.com', '2023-08-20'),
];

function createData(id, name, surname, email, joinDate) {
  return { id, name, surname, email, joinDate };
}

const HomeUserTable = () => {

  return (
    <StyledTableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }} aria-label="customized table">
        <StyledTableHead>
          <TableRow>
            <StyledTableCell>Id_user</StyledTableCell>
            <StyledTableCell align="center">Imię</StyledTableCell>
            <StyledTableCell align="center">Nazwisko</StyledTableCell>
            <StyledTableCell align="center">Email</StyledTableCell>
            <StyledTableCell align="right">Data dołączenia</StyledTableCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.id}>
              <StyledTableCell component="th" scope="row">
                {row.id}
              </StyledTableCell>
              <StyledTableCell align="center">{row.name}</StyledTableCell>
              <StyledTableCell align="center">{row.surname}</StyledTableCell>
              <StyledTableCell align="center">{row.email}</StyledTableCell>
              <StyledTableCell align="right">{row.joinDate}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}
export default HomeUserTable;