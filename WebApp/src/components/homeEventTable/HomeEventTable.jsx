import * as React from 'react';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import "./homeEventTable.scss";

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
  createData(1, 4, 'Wycieczka do ZOO', 'Zapraszamy na niezapomnianą wycieczkę do zoo! Odkryj fascynujący świat dzikiej przyrody...', '2023-10-15'),
  createData(2, 3, 'Wyjście do kina', 'Przyjdź do kina! Czekają na Ciebie najnowsze hity filmowe i wyjątkowa atmosfera, którą możesz...', '2023-08-20'),
];

function createData(Id_event, Id_user, title, description, report_date) {
  return { Id_event, Id_user, title, description, report_date };
}

const HomeEventTable = () => {

  return (
    <StyledTableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }} aria-label="customized table">
        <StyledTableHead>
          <TableRow>
            <StyledTableCell>Id_event</StyledTableCell>
            <StyledTableCell align="center">Id_user</StyledTableCell>
            <StyledTableCell align="center">Tytuł</StyledTableCell>
            <StyledTableCell align="center">Opis</StyledTableCell>
            <StyledTableCell align="right">Data utworzenia</StyledTableCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.Id_event}>
              <StyledTableCell component="th" scope="row">
                {row.Id_event}
              </StyledTableCell>
              <StyledTableCell align="center">{row.Id_user}</StyledTableCell>
              <StyledTableCell align="center">{row.title}</StyledTableCell>
              <StyledTableCell align="center">{row.description}</StyledTableCell>
              <StyledTableCell align="right">{row.report_date}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}
export default HomeEventTable;