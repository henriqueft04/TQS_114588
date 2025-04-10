import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { RestaurantApi } from '../api/RestaurantApi';

export default function RestaurantSchedule() {
  const { id } = useParams();
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSchedule = async () => {
      try {
        const data = await RestaurantApi.getSchedule(id);
        setSchedule(data);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchSchedule();
  }, [id]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="container mx-auto">
      <h1 className="text-3xl font-bold mb-4">Restaurant Schedule</h1>
      {schedule.map((day) => (
        <div key={day.day} className="mb-8">
          <h2 className="text-2xl font-bold mb-2">{day.day}</h2>
          <table className="table-auto w-full">
            <thead>
              <tr>
                <th className="px-4 py-2">Meal</th>
                <th className="px-4 py-2">Start Time</th>
                <th className="px-4 py-2">End Time</th>
              </tr>
            </thead>
            <tbody>
              {day.meals.map((meal) => (
                <tr key={meal.id}>
                  <td className="border px-4 py-2">{meal.name}</td>
                  <td className="border px-4 py-2">{meal.startTime}</td>
                  <td className="border px-4 py-2">{meal.endTime}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
} 