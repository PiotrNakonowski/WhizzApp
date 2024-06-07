import * as React from 'react';
import { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { collection, getDocs, query, orderBy, limit } from 'firebase/firestore';
import { db } from '../../firebase';
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
  height: 90,
}));

const StyledTableHead = styled(TableHead)({
  backgroundColor: '#616161',
});

const StyledTableContainer = styled(TableContainer)({
  marginBottom: '35px',
});

const HomeEventTable = () => {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const q = query(collection(db, 'events'),orderBy('CreatedAt', 'desc'),limit(2));
        const querySnapshot = await getDocs(q);
        const events = querySnapshot.docs.map(doc => ({
          Id_event: doc.id,
          Id_user: doc.data().Owner,
          title: doc.data().Title,
          description: doc.data().Description,
          date: doc.data().CreatedAt?.toDate().toISOString().split('T')[0]
        }));
        setRows(events);
      } catch (error) {
        console.error('Error fetching events: ', error);
      }
    };

    fetchEvents();
  }, []);

  return (
    <StyledTableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }}  aria-label="customized table">
        <StyledTableHead>
          <TableRow>
            <StyledTableCell style={{ width: '10%' }}>Id_event</StyledTableCell>
            <StyledTableCell style={{ width: '10%' }} align="center">Id_user</StyledTableCell>
            <StyledTableCell style={{ width: '20%' }} align="center">Tytuł</StyledTableCell>
            <StyledTableCell style={{ width: '40%' }} align="center">Opis</StyledTableCell>
            <StyledTableCell style={{ width: '20%' }} align="right">Data utworzenia</StyledTableCell>
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
              <StyledTableCell align="center" style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis'/*to ostanie dodaje kropki*/}}>{row.description}</StyledTableCell>
              <StyledTableCell align="right">{row.date}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}

export default HomeEventTable;