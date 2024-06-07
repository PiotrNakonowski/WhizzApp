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
import "./homeReportTable.scss";

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
  '&:nth-of-type(even)': {
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

const HomeReportTable = () => {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    const fetchReports = async () => {
      try {
        const q = query(collection(db, 'reports'),orderBy('Date', 'desc'),limit(1));
        const querySnapshot = await getDocs(q);
        const reports = querySnapshot.docs.map(doc => ({
          Id_report: doc.id,
          Id_user: doc.data().Owner,
          description: doc.data().Description,
          date: doc.data().Date?.toDate().toISOString().split('T')[0]
        }));
        setRows(reports);
      } catch (error) {
        console.error('Error fetching reports: ', error);
      }
    };

    fetchReports();
  }, []);

  return (
    <StyledTableContainer component={Paper}>
      <Table sx={{ minWidth: 700 }}  aria-label="customized table">
        <StyledTableHead>
          <TableRow>
            <StyledTableCell style={{ width: '10%' }}>Id_report</StyledTableCell>
            <StyledTableCell style={{ width: '10%' }} align="center">Id_user</StyledTableCell>
            <StyledTableCell style={{ width: '40%' }} align="center">Opis</StyledTableCell>
            <StyledTableCell style={{ width: '20%' }} align="right">Data utworzenia</StyledTableCell>
          </TableRow>
        </StyledTableHead>
        <TableBody>
          {rows.map((row) => (
            <StyledTableRow key={row.Id_report}>
              <StyledTableCell component="th" scope="row">
                {row.Id_report}
              </StyledTableCell>
              <StyledTableCell align="center">{row.Id_user}</StyledTableCell>
              <StyledTableCell align="center" style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis'/*to ostanie dodaje kropki*/}}>{row.description}</StyledTableCell>
              <StyledTableCell align="right">{row.date}</StyledTableCell>
            </StyledTableRow>
          ))}
        </TableBody>
      </Table>
    </StyledTableContainer>
  );
}

export default HomeReportTable;
